class CreateEpisodes < ActiveRecord::Migration[5.1]
  def change
    create_table :episodes do |t|
      t.string :title
      t.string :episode
      t.string :summary
      t.string :image
      t.references :season, foreign_key: true

      t.timestamps
    end
  end
end
