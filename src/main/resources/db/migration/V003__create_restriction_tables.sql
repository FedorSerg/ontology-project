DO
$$
    DECLARE
        lScriptName  VARCHAR := 'create restriction tables';
        lErrorStack  TEXT;
        lErrorState  TEXT;
        lErrorMsg    TEXT;
        lErrorDetail TEXT;
        lErrorHint   TEXT;
    BEGIN
        RAISE NOTICE 'Start of % ...', lScriptName;

        create table if not exists restriction_attribute
        (
            id   bigserial not null
                constraint restriction_attribute_pkey
                    primary key,
            name text unique
        );

        create table if not exists restriction_relation
        (
            id   bigserial not null
                constraint restriction_relation_pkey
                    primary key,
            name text unique
        );

    EXCEPTION
        WHEN OTHERS
            THEN
                GET STACKED DIAGNOSTICS
                    lErrorState = RETURNED_SQLSTATE,
                    lErrorMsg = MESSAGE_TEXT,
                    lErrorDetail = PG_EXCEPTION_DETAIL,
                    lErrorHint = PG_EXCEPTION_HINT,
                    lErrorStack = PG_EXCEPTION_CONTEXT;
                RAISE EXCEPTION ' in script during executing.
  code        : %
  message     : %
  description : %
  hint        : %
  context     : %', lErrorState, lErrorMsg, lErrorDetail, lErrorHint, lErrorStack;
    END
$$
