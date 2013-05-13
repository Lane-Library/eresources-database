drop index TEXT_IDX;

begin
  ctx_ddl.drop_preference('"TEXT_IDX_DST"');
  ctx_ddl.create_preference('"TEXT_IDX_DST"','DIRECT_DATASTORE');                    
end;                                                                            
/                                                                               
                                                                                
begin
  ctx_ddl.drop_preference('"TEXT_IDX_FIL"');
  ctx_ddl.create_preference('"TEXT_IDX_FIL"','NULL_FILTER');                                                                          
end;                                                                            
/                                                                               
                                                                                
begin
  ctx_ddl.drop_section_group('"TEXT_IDX_SGP"');
  ctx_ddl.create_section_group('"TEXT_IDX_SGP"','NULL_SECTION_GROUP');                                                                     
end;                                                                            
/                                                                               
                                                                                
begin
  ctx_ddl.drop_preference('"TEXT_IDX_LEX"');
  ctx_ddl.create_preference('"TEXT_IDX_LEX"','BASIC_LEXER'); 
  ctxsys.ctx_ddl.set_attribute ('"TEXT_IDX_LEX"','base_letter','YES');
end;                                                                            
/                                                                               
                                                                                
begin
  ctx_ddl.drop_preference('"TEXT_IDX_WDL"');
  ctx_ddl.create_preference('"TEXT_IDX_WDL"','BASIC_WORDLIST');                                                                            
  ctx_ddl.set_attribute('"TEXT_IDX_WDL"','STEMMER','ENGLISH');                                                                         
  ctx_ddl.set_attribute('"TEXT_IDX_WDL"','FUZZY_MATCH','GENERIC');                                                                  
end;                                                                            
/                                                                               
                                                                                
begin
  ctx_ddl.drop_stoplist('"TEXT_IDX_SPL"');
  ctx_ddl.create_stoplist('"TEXT_IDX_SPL"','BASIC_STOPLIST');                                                                      
  ctx_ddl.add_stopword('"TEXT_IDX_SPL"','Mr');                                  
  ctx_ddl.add_stopword('"TEXT_IDX_SPL"','Mrs');                                                                           
  ctx_ddl.add_stopword('"TEXT_IDX_SPL"','Ms');                                  
  ctx_ddl.add_stopword('"TEXT_IDX_SPL"','a');                                   
  ctx_ddl.add_stopword('"TEXT_IDX_SPL"','all');                                                                          
  ctx_ddl.add_stopword('"TEXT_IDX_SPL"','almost');                              
  ctx_ddl.add_stopword('"TEXT_IDX_SPL"','also');                                                                          
  ctx_ddl.add_stopword('"TEXT_IDX_SPL"','although');                            
  ctx_ddl.add_stopword('"TEXT_IDX_SPL"','an');                                                                          
  ctx_ddl.add_stopword('"TEXT_IDX_SPL"','and');                                 
  ctx_ddl.add_stopword('"TEXT_IDX_SPL"','any');
  ctx_ddl.add_stopword('"TEXT_IDX_SPL"','are');                                 
  ctx_ddl.add_stopword('"TEXT_IDX_SPL"','as');                                  
  ctx_ddl.add_stopword('"TEXT_IDX_SPL"','at');                                                                            
  ctx_ddl.add_stopword('"TEXT_IDX_SPL"','be');                                  
  ctx_ddl.add_stopword('"TEXT_IDX_SPL"','because');                                                                             
  ctx_ddl.add_stopword('"TEXT_IDX_SPL"','been');                                
  ctx_ddl.add_stopword('"TEXT_IDX_SPL"','both');                                                                               
  ctx_ddl.add_stopword('"TEXT_IDX_SPL"','but');                                 
  ctx_ddl.add_stopword('"TEXT_IDX_SPL"','by');                                  
  ctx_ddl.add_stopword('"TEXT_IDX_SPL"','can');                                                                            
  ctx_ddl.add_stopword('"TEXT_IDX_SPL"','could');                               
  ctx_ddl.add_stopword('"TEXT_IDX_SPL"','d');                                   
  ctx_ddl.add_stopword('"TEXT_IDX_SPL"','did');                                 
  ctx_ddl.add_stopword('"TEXT_IDX_SPL"','do');                                  
  ctx_ddl.add_stopword('"TEXT_IDX_SPL"','does');                                                                            
  ctx_ddl.add_stopword('"TEXT_IDX_SPL"','either');                              
  ctx_ddl.add_stopword('"TEXT_IDX_SPL"','for');                                                                             
  ctx_ddl.add_stopword('"TEXT_IDX_SPL"','from');                                
  ctx_ddl.add_stopword('"TEXT_IDX_SPL"','had');                                 
  ctx_ddl.add_stopword('"TEXT_IDX_SPL"','has');                                 
  ctx_ddl.add_stopword('"TEXT_IDX_SPL"','have');                                
  ctx_ddl.add_stopword('"TEXT_IDX_SPL"','having');                                                                        
  ctx_ddl.add_stopword('"TEXT_IDX_SPL"','he');                                  
  ctx_ddl.add_stopword('"TEXT_IDX_SPL"','her');                                                                             
  ctx_ddl.add_stopword('"TEXT_IDX_SPL"','here');                                
  ctx_ddl.add_stopword('"TEXT_IDX_SPL"','hers');                                                                               
  ctx_ddl.add_stopword('"TEXT_IDX_SPL"','him');                                 
  ctx_ddl.add_stopword('"TEXT_IDX_SPL"','his');                                 
  ctx_ddl.add_stopword('"TEXT_IDX_SPL"','how');                                                                           
  ctx_ddl.add_stopword('"TEXT_IDX_SPL"','however');                             
  ctx_ddl.add_stopword('"TEXT_IDX_SPL"','i');                                                                             
  ctx_ddl.add_stopword('"TEXT_IDX_SPL"','if');                                  
  ctx_ddl.add_stopword('"TEXT_IDX_SPL"','in');                                  
  ctx_ddl.add_stopword('"TEXT_IDX_SPL"','into');                                                                          
  ctx_ddl.add_stopword('"TEXT_IDX_SPL"','is');                                  
  ctx_ddl.add_stopword('"TEXT_IDX_SPL"','it');                                  
  ctx_ddl.add_stopword('"TEXT_IDX_SPL"','its');                                 
  ctx_ddl.add_stopword('"TEXT_IDX_SPL"','just');                                
  ctx_ddl.add_stopword('"TEXT_IDX_SPL"','ll');                                                                            
  ctx_ddl.add_stopword('"TEXT_IDX_SPL"','me');                                  
  ctx_ddl.add_stopword('"TEXT_IDX_SPL"','might');                                                                               
  ctx_ddl.add_stopword('"TEXT_IDX_SPL"','my');                                  
  ctx_ddl.add_stopword('"TEXT_IDX_SPL"','no');                                  
  ctx_ddl.add_stopword('"TEXT_IDX_SPL"','non');                                                                             
  ctx_ddl.add_stopword('"TEXT_IDX_SPL"','nor');                                 
  ctx_ddl.add_stopword('"TEXT_IDX_SPL"','not');                                 
  ctx_ddl.add_stopword('"TEXT_IDX_SPL"','of');                                                                          
  ctx_ddl.add_stopword('"TEXT_IDX_SPL"','on');                                  
  ctx_ddl.add_stopword('"TEXT_IDX_SPL"','one');                                                                               
  ctx_ddl.add_stopword('"TEXT_IDX_SPL"','only');                                
  ctx_ddl.add_stopword('"TEXT_IDX_SPL"','onto');                                
  ctx_ddl.add_stopword('"TEXT_IDX_SPL"','or');                                                                          
  ctx_ddl.add_stopword('"TEXT_IDX_SPL"','our');                                 
  ctx_ddl.add_stopword('"TEXT_IDX_SPL"','ours');                                                                             
  ctx_ddl.add_stopword('"TEXT_IDX_SPL"','s');                                   
  ctx_ddl.add_stopword('"TEXT_IDX_SPL"','shall');                               
  ctx_ddl.add_stopword('"TEXT_IDX_SPL"','she');                                                                         
  ctx_ddl.add_stopword('"TEXT_IDX_SPL"','should');                              
  ctx_ddl.add_stopword('"TEXT_IDX_SPL"','since');                                                                        
  ctx_ddl.add_stopword('"TEXT_IDX_SPL"','so');                                  
  ctx_ddl.add_stopword('"TEXT_IDX_SPL"','some');                                                                            
  ctx_ddl.add_stopword('"TEXT_IDX_SPL"','still');                               
  ctx_ddl.add_stopword('"TEXT_IDX_SPL"','such');                                                                             
  ctx_ddl.add_stopword('"TEXT_IDX_SPL"','t');                                   
  ctx_ddl.add_stopword('"TEXT_IDX_SPL"','than');                                
  ctx_ddl.add_stopword('"TEXT_IDX_SPL"','that');                                                                         
  ctx_ddl.add_stopword('"TEXT_IDX_SPL"','the');                                 
  ctx_ddl.add_stopword('"TEXT_IDX_SPL"','thir');                                                                           
  ctx_ddl.add_stopword('"TEXT_IDX_SPL"','them');                                
  ctx_ddl.add_stopword('"TEXT_IDX_SPL"','then');                                                                             
  ctx_ddl.add_stopword('"TEXT_IDX_SPL"','there');                               
  ctx_ddl.add_stopword('"TEXT_IDX_SPL"','therefore');                                                                         
  ctx_ddl.add_stopword('"TEXT_IDX_SPL"','these');                               
  ctx_ddl.add_stopword('"TEXT_IDX_SPL"','they');                                                                          
  ctx_ddl.add_stopword('"TEXT_IDX_SPL"','this');                                
  ctx_ddl.add_stopword('"TEXT_IDX_SPL"','those');                                                                           
  ctx_ddl.add_stopword('"TEXT_IDX_SPL"','though');                              
  ctx_ddl.add_stopword('"TEXT_IDX_SPL"','through');                                                                        
  ctx_ddl.add_stopword('"TEXT_IDX_SPL"','thus');                                
  ctx_ddl.add_stopword('"TEXT_IDX_SPL"','to');                                                                            
  ctx_ddl.add_stopword('"TEXT_IDX_SPL"','too');                                 
  ctx_ddl.add_stopword('"TEXT_IDX_SPL"','until');                                                                              
  ctx_ddl.add_stopword('"TEXT_IDX_SPL"','ve');                                  
  ctx_ddl.add_stopword('"TEXT_IDX_SPL"','very');                                
  ctx_ddl.add_stopword('"TEXT_IDX_SPL"','was');                                                                          
  ctx_ddl.add_stopword('"TEXT_IDX_SPL"','we');                                  
  ctx_ddl.add_stopword('"TEXT_IDX_SPL"','were');                                                                              
  ctx_ddl.add_stopword('"TEXT_IDX_SPL"','what');                                
  ctx_ddl.add_stopword('"TEXT_IDX_SPL"','when');                                
  ctx_ddl.add_stopword('"TEXT_IDX_SPL"','where');                               
  ctx_ddl.add_stopword('"TEXT_IDX_SPL"','whether');                                                                              
  ctx_ddl.add_stopword('"TEXT_IDX_SPL"','which');                               
  ctx_ddl.add_stopword('"TEXT_IDX_SPL"','while');                                                                              
  ctx_ddl.add_stopword('"TEXT_IDX_SPL"','who');                                 
  ctx_ddl.add_stopword('"TEXT_IDX_SPL"','whose');                               
  ctx_ddl.add_stopword('"TEXT_IDX_SPL"','why');                                 
  ctx_ddl.add_stopword('"TEXT_IDX_SPL"','will');                                
  ctx_ddl.add_stopword('"TEXT_IDX_SPL"','with');                                                                          
  ctx_ddl.add_stopword('"TEXT_IDX_SPL"','would');                               
  ctx_ddl.add_stopword('"TEXT_IDX_SPL"','yet');                                                                            
  ctx_ddl.add_stopword('"TEXT_IDX_SPL"','you');                                 
  ctx_ddl.add_stopword('"TEXT_IDX_SPL"','your');                                                                               
  ctx_ddl.add_stopword('"TEXT_IDX_SPL"','yours');                               
end;                                                                            
/                                                                               
                                                                                
begin
  ctx_ddl.drop_preference('"TEXT_IDX_STO"');
  ctx_ddl.create_preference('"TEXT_IDX_STO"','BASIC_STORAGE');                                                                        
  ctx_ddl.set_attribute('"TEXT_IDX_STO"','R_TABLE_CLAUSE','lob (data) store as (cache)');                                          
  ctx_ddl.set_attribute('"TEXT_IDX_STO"','I_INDEX_CLAUSE','compress 2');                                                 
end;                                                                            
/                                                                               
                                                                                
                                                                                
begin                                                                           
  ctx_output.start_log('TEXT_IDX_LOG');                                         
end;                                                                            
/                                                                               
                                                                                
create index "TEXT_IDX"                                               
  on "ERESOURCE"                                                      
      ("TEXT")                                                                  
  indextype is ctxsys.context                                                   
  parameters('                                                                  
    datastore       "TEXT_IDX_DST"                                              
    filter          "TEXT_IDX_FIL"                                              
    section group   "TEXT_IDX_SGP"                                              
    lexer           "TEXT_IDX_LEX"                                              
    wordlist        "TEXT_IDX_WDL"                                              
    stoplist        "TEXT_IDX_SPL"                                              
    storage         "TEXT_IDX_STO"                                              
  ')                                                                            
/                                                                               
                                                                                
begin                                                                           
  ctx_output.end_log;                                                           
end;                                                                            
/   