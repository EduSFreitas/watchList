class Season < ApplicationRecord
  belongs_to :tvshow
  has_many :episodes
end
