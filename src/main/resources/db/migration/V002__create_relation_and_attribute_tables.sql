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

        create table if not exists attribute
        (
            id          bigserial not null
                constraint attribute_pkey
                    primary key,
            name        text unique,
            range       text,
            domain_id   bigserial
                constraint attribute_class_ref
                    references class,
            ontology_id bigserial
                constraint attribute_ontology_ref
                    references ontology
        );

        create table if not exists attribute_value
        (
            id           bigserial not null
                constraint attribute_value_pkey
                    primary key,
            attribute_id bigserial
                constraint attribute_value_attribute_ref
                    references attribute,
            instance_id  bigserial
                constraint attribute_value_instance_ref
                    references instance,
            value        text,
            ontology_id  bigserial
                constraint attribute_value_ontology_ref
                    references ontology
        );

        create table if not exists relation
        (
            id          bigserial not null
                constraint relation_pkey
                    primary key,
            name        text unique,
            domain_id   bigserial
                constraint relation_domain_ref
                    references class,
            range_id    bigserial
                constraint relation_range_ref
                    references class,
            ontology_id bigserial
                constraint relation_ontology_ref
                    references ontology
        );

        create table if not exists relation_instance
        (
            id          bigserial not null
                constraint relation_instance_pkey
                    primary key,
            relation_id bigserial
                constraint relation_instance_relation_ref
                    references relation,
            domain_id   bigserial
                constraint relation_instance_domain_ref
                    references instance,
            range_id    bigserial
                constraint relation_instance_range_ref
                    references instance,
            ontology_id bigserial
                constraint relation_instance_ontology_ref
                    references ontology
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
