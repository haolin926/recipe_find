drop table recipe_db.comment_images;
drop table recipe_db.comments;
drop table recipe_db.meal_plan_recipes;
drop table recipe_db.recipe_ingredients;
drop table recipe_db.recipe_nutrition;
drop table recipe_db.saved_recipe;
drop table recipe_db.meal_plans;
drop table recipe_db.users;
drop table recipe_db.recipes;
drop table recipe_db.ingredients;
drop table recipe_db.nutrition;

create table if not exists recipe_db.users
(
    user_id  bigint generated always as identity
        primary key,
    username varchar(255) not null,
    email    varchar(255) not null,
    password varchar(255) not null
);

alter table recipe_db.users
    owner to postgres;

create table if not exists recipe_db.recipes
(
    recipe_id          bigint generated always as identity
        primary key,
    recipe_api_id      integer      not null,
    image_url          varchar(255),
    recipe_name        varchar(255) not null,
    recipe_description varchar(255),
    instruction        jsonb        not null,
    dairy_free         boolean,
    gluten_free        boolean,
    vegetarian         boolean,
    cook_time          integer
);

alter table recipe_db.recipes
    owner to postgres;

create table if not exists recipe_db.saved_recipe
(
    user_id         bigint not null
        references recipe_db.users
            on delete cascade,
    recipe_id       bigint not null
        references recipe_db.recipes
            on delete cascade,
    saved_recipe_id integer generated always as identity
        primary key
);

alter table recipe_db.saved_recipe
    owner to postgres;

create table if not exists recipe_db.meal_plans
(
    meal_plan_id integer generated always as identity
        primary key,
    user_id      bigint not null
        constraint fk7friea1stnx97lswpyxlb9ln1
            references recipe_db.users,
    plan_date    date   not null
);

alter table recipe_db.meal_plans
    owner to postgres;

create table if not exists recipe_db.meal_plan_recipes
(
    meal_plan_id        integer not null
        references recipe_db.meal_plans
            on delete cascade,
    recipe_id           bigint  not null
        references recipe_db.recipes
            on delete cascade,
    meal_plan_recipe_id integer generated always as identity
        primary key
);

alter table recipe_db.meal_plan_recipes
    owner to postgres;

create table if not exists recipe_db.comments
(
    comment_id      integer generated always as identity
        primary key,
    user_id         integer      not null
        references recipe_db.users
            on delete cascade,
    recipe_id       integer      not null
        references recipe_db.recipes
            on delete cascade,
    comment_content varchar(500) not null,
    rate            double precision
);

alter table recipe_db.comments
    owner to postgres;

create table if not exists recipe_db.nutrition
(
    nutrition_id   integer generated always as identity
        constraint nutritions_pkey
            primary key,
    nutrition_name varchar(100) not null
);

alter table recipe_db.nutrition
    owner to postgres;

create table if not exists recipe_db.recipe_nutrition
(
    nutrition_id        integer not null
        constraint recipe_nutritions_nutrition_id_fkey
            references recipe_db.nutrition
            on delete cascade,
    recipe_id           bigint  not null
        constraint recipe_nutritions_recipe_id_fkey
            references recipe_db.recipes
            on delete cascade,
    recipe_nutrition_id integer generated always as identity
        constraint recipe_nutritions_pkey
            primary key,
    amount              numeric(38, 2),
    unit                varchar(20)
);

alter table recipe_db.recipe_nutrition
    owner to postgres;

create table if not exists recipe_db.ingredients
(
    ingredient_id   bigint generated always as identity
        primary key,
    ingredient_name varchar(100) not null
);

alter table recipe_db.ingredients
    owner to postgres;

create table if not exists recipe_db.recipe_ingredients
(
    ingredient_id        bigint         not null
        references recipe_db.ingredients
            on delete cascade,
    recipe_id            bigint         not null
        references recipe_db.recipes
            on delete cascade,
    ingredient_amount    numeric(38, 2) not null,
    ingredient_unit      varchar(100)   not null,
    recipe_ingredient_id integer generated always as identity
        primary key
);

alter table recipe_db.recipe_ingredients
    owner to postgres;

create table if not exists recipe_db.comment_images
(
    comment_id       integer
        constraint comment_image_comments_id_fk
            references recipe_db.comments
            on delete cascade,
    image            text not null,
    comment_image_id integer generated always as identity
);

alter table recipe_db.comment_images
    owner to postgres;