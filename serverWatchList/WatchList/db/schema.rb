# This file is auto-generated from the current state of the database. Instead
# of editing this file, please use the migrations feature of Active Record to
# incrementally modify your database, and then regenerate this schema definition.
#
# Note that this schema.rb definition is the authoritative source for your
# database schema. If you need to create the application database on another
# system, you should be using db:schema:load, not running all the migrations
# from scratch. The latter is a flawed and unsustainable approach (the more migrations
# you'll amass, the slower it'll run and the greater likelihood for issues).
#
# It's strongly recommended that you check this file into your version control system.

ActiveRecord::Schema.define(version: 20180325110949) do

  create_table "episodes", force: :cascade do |t|
    t.string "title"
    t.string "episode"
    t.string "summary"
    t.string "image"
    t.integer "season_id"
    t.datetime "created_at", null: false
    t.datetime "updated_at", null: false
    t.index ["season_id"], name: "index_episodes_on_season_id"
  end

  create_table "seasons", force: :cascade do |t|
    t.integer "season"
    t.integer "tvshow_id"
    t.datetime "created_at", null: false
    t.datetime "updated_at", null: false
    t.index ["tvshow_id"], name: "index_seasons_on_tvshow_id"
  end

  create_table "tvshows", force: :cascade do |t|
    t.string "title"
    t.string "category"
    t.datetime "created_at", null: false
    t.datetime "updated_at", null: false
  end

end
