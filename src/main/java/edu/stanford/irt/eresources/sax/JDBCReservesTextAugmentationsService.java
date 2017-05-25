package edu.stanford.irt.eresources.sax;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import edu.stanford.irt.eresources.EresourceDatabaseException;

public class JDBCReservesTextAugmentationsService implements AugmentationsService {

    private static final String SQL = "SELECT bib_item.bib_id, department_name, course_number, "
            + "  last_name, first_name FROM lmldb.reserve_list_items, lmldb.reserve_list, "
            + "  lmldb.reserve_list_courses, lmldb.course,  lmldb.bib_item, lmldb.bib_text, "
            + "  lmldb.instructor, lmldb.department "
            + "WHERE reserve_list.reserve_list_id      = reserve_list_items.reserve_list_id "
            + "AND reserve_list.reserve_list_id        = reserve_list_courses.reserve_list_id "
            + "AND reserve_list_courses.department_id != 22 "
            + "AND course.course_id                    = reserve_list_courses.course_id "
            + "AND instructor.instructor_id            = reserve_list_courses.instructor_id "
            + "AND department.department_id            = reserve_list_courses.department_id "
            + "AND bib_item.item_id                    = reserve_list_items.item_id "
            + "AND bib_text.bib_id                     = bib_item.bib_id AND bib_item.bib_id NOT IN "
            + " (SELECT bib_id FROM lmldb.bib_index WHERE index_code   = '655H' AND normal_heading = 'OBJECTS'  ) "
            + "AND expire_date > SYSDATE GROUP BY bib_item.bib_id, department_name, course_number, "
            + "  last_name, first_name ORDER BY bib_item.bib_id";

    private DataSource dataSource;

    public JDBCReservesTextAugmentationsService(final DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Map<String, String> buildAugmentations() {
        Map<String, String> augmentations = new HashMap<>();
        try (Connection conn = this.dataSource.getConnection();
                PreparedStatement getListStmt = conn.prepareStatement(SQL);
                ResultSet rs = getListStmt.executeQuery();) {
            int columnCount = rs.getMetaData().getColumnCount();
            while (rs.next()) {
                String bibId = rs.getString(1);
                StringBuilder sb = new StringBuilder();
                for (int i = 2; i <= columnCount; i++) {
                    // kludge instead of adding reserves-specific fields
                    sb.append("reserves:");
                    sb.append(rs.getString(i));
                    sb.append(' ');
                }
                if (augmentations.containsKey(bibId)) {
                    sb.append(augmentations.get(bibId));
                }
                augmentations.put(bibId, sb.toString());
            }
        } catch (SQLException e) {
            throw new EresourceDatabaseException(e);
        }
        return augmentations;
    }
}
