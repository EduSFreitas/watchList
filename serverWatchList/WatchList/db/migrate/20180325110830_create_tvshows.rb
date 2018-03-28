class CreateTvshows < ActiveRecord::Migration[5.1]
  def change
    create_table :tvshows do |t|
      t.string :title
      t.string :category
      

      t.timestamps
    end
  end
end
