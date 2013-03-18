ALTER TABLE localizedstrings RENAME TO localizedstring;
ALTER TABLE intlstring RENAME TO internationalstring;
ALTER TABLE intlstring_localizedstrings RENAME TO internationalstring_localizedstring;
ALTER TABLE internationalstring_localizedstring RENAME COLUMN intlstring_key TO internationalstring_key;
ALTER TABLE serviceinterfacetype RENAME TO serviceinterface;
ALTER TABLE stringqueryexpressiontype RENAME TO stringqueryexpression;
ALTER TABLE vocabularytermtype RENAME TO vocabularyterm;
ALTER TABLE xmlqueryexpressiontype RENAME TO xmlqueryexpression;
ALTER TABLE queryexpressiontype RENAME TO queryexpression;
ALTER TABLE queryexpressiontype_slot RENAME TO queryexpression_slot;
ALTER TABLE maptype RENAME TO map;
ALTER TABLE entrytype RENAME TO entry;
ALTER TABLE maptype_entrytype RENAME TO map_entry;
