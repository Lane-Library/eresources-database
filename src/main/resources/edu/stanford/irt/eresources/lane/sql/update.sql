DECLARE
  CURSOR bib_mfhd_cursor
  IS
    SELECT DISTINCT bib_mfhd.bib_id,
      bib_mfhd.mfhd_id
    FROM lmldb.bib_mfhd,
      lmldb.mfhd_master,
      lmldb.bib_master
    WHERE mfhd_master.mfhd_id         = bib_mfhd.mfhd_id
    AND bib_master.bib_id             = bib_mfhd.bib_id
    AND mfhd_master.suppress_in_opac != 'Y'
    AND bib_master.suppress_in_opac  != 'Y'
    AND bib_master.bib_id            IN
      (SELECT bib_id
      FROM lmldb.bib_master
      WHERE update_date > TO_TIMESTAMP ('{timestamp}', 'YYYY-MM-DD HH24:MI:SS.FF')
      OR create_date    > TO_TIMESTAMP ('{timestamp}', 'YYYY-MM-DD HH24:MI:SS.FF')
    UNION
    SELECT bib_mfhd.bib_id
    FROM lmldb.bib_mfhd,
      lmldb.mfhd_master
    WHERE mfhd_master.mfhd_id = bib_mfhd.mfhd_id
    AND (update_date          > TO_TIMESTAMP ('{timestamp}', 'YYYY-MM-DD HH24:MI:SS.FF')
    OR create_date            > TO_TIMESTAMP ('{timestamp}', 'YYYY-MM-DD HH24:MI:SS.FF'))
    UNION
    SELECT bib_item.bib_id
    FROM lmldb.bib_item,
      lmldb.item_status item_status_1
    LEFT OUTER JOIN lmldb.item_status item_status_2
    ON (item_status_1.item_id          = item_status_2.item_id
    AND item_status_1.item_status_date < item_status_2.item_status_date)
    WHERE item_status_2.item_id       IS NULL
    AND bib_item.item_id               = item_status_1.item_id
    AND item_status_1.item_status_date > TO_TIMESTAMP ('{timestamp}', 'YYYY-MM-DD HH24:MI:SS.FF')
      )
    AND bib_master.bib_id NOT IN
      ( SELECT DISTINCT bib_id
      FROM lmldb.bib_index
      WHERE index_code = '0350'
      AND NORMAL_HEADING LIKE 'PMID %'
      AND bib_id IN
        ( SELECT DISTINCT bib_id
        FROM lmldb.bib_index
        WHERE index_code = '655H'
        AND NORMAL_HEADING LIKE 'ARTICLES'
        )
      )
    ORDER BY bib_id,
      mfhd_id;
  bibid NUMBER(8,0);
  bibblob CLOB;
  updates CLOB;
BEGIN
  DBMS_LOB.CREATETEMPORARY(updates, TRUE, DBMS_LOB.SESSION);
  FOR i IN bib_mfhd_cursor
    LOOP
      IF i.bib_id = bibid THEN
        bibblob  := lmldb.getMfhdBlob(i.mfhd_id);
        DBMS_LOB.COPY(updates, bibblob, DBMS_LOB.GETLENGTH(bibblob),DBMS_LOB.GETLENGTH(updates) + 1 ,1);
      ELSE
        bibblob := lmldb.getBibBlob(i.bib_id);
        DBMS_LOB.COPY(updates, bibblob, DBMS_LOB.GETLENGTH(bibblob),DBMS_LOB.GETLENGTH(updates) + 1 ,1);
        bibblob  := lmldb.getMfhdBlob(i.mfhd_id);
        DBMS_LOB.COPY(updates, bibblob, DBMS_LOB.GETLENGTH(bibblob),DBMS_LOB.GETLENGTH(updates) + 1 ,1);
        bibid   := i.bib_id;
      END IF;
  END LOOP;
  ? := updates;
END;