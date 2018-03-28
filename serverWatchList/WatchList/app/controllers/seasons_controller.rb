class SeasonsController < ApplicationController
  def index
    render json: @seasons = Season.where(tvshow_id: params[:tvshow_id])
  end

  def show
    render json: @season = Season.find(params[:id])
  end
end
