DO
$$
    DECLARE
        lScriptName  VARCHAR := 'create ontology tables';
        lErrorStack  TEXT;
        lErrorState  TEXT;
        lErrorMsg    TEXT;
        lErrorDetail TEXT;
        lErrorHint   TEXT;
    BEGIN
        RAISE NOTICE 'Start of % ...', lScriptName;

        create table if not exists ontology
        (
            id  bigserial not null
                constraint ontology_pkey
                    primary key,
            iri text unique
        );

        create table if not exists ontology_prefix
        (
            id   bigserial not null
                constraint ontology_prefix_pkey
                    primary key,
            name text,
            iri  text,
            ontology_id bigserial
                constraint class_ontology_ref
                    references ontology
        );

        create table if not exists class
        (
            id          bigserial not null
                constraint class_pkey
                    primary key,
            name        text unique,
            ontology_id bigserial
                constraint class_ontology_ref
                    references ontology
        );

        create table if not exists class_superclasses
        (
            class_entity_id bigserial
                constraint class_superclasses_class_ref
                    references class,
            superclasses_id bigserial
                constraint class_superclasses_superclass_ref
                    references class
        );

        create table if not exists instance
        (
            id       bigserial not null
                constraint instance_pkey
                    primary key,
            name     text,
            class_id bigserial
                constraint instance_class_ref
                    references class
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
