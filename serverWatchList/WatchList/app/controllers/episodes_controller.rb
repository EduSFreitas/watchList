class EpisodesController < ApplicationController
  def index
    render json: @episodes = Episode.where(season_id: params[:season_id])
  end

  def show
    render json: @episode = Episode.find(params[:id])
  end
end
