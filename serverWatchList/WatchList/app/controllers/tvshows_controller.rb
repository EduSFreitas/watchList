require 'open-uri'
class TvshowsController < ApplicationController
  skip_before_action :verify_authenticity_token
  def index
    @tv_shows = Tvshow.all
    render json: @tv_shows
  end

  def show
    @tv_show = Tvshow.find(params[:id])
    render json: @tv_show
  end

  def create
    title = params[:title].titleize
    @tv_show = Tvshow.where(title: title, category: params[:category])
    if @tv_show.count > 0
      return render json: {success: false}
    end
    api = 'http://api.tvmaze.com/singlesearch/shows?embed=episodes&q='
    query = api + title
    begin
      response = open(query)
    rescue
      return render json: {success: false}
    end
    resp = JSON.parse(response.read)
    episodes = resp['_embedded']['episodes']
    @tv_show = Tvshow.new
    @tv_show.title = title
    @tv_show.category = params[:category]
    @tv_show.save
    episodes.each do |episode|
      season = episode['season']
      @season = Season.where(season: season, tvshow: @tv_show)
      if @season.count == 0
        @season = Season.new(season: season, tvshow: @tv_show)
        @season.save
        else
          @season = @season.first
      end
      @episode = Episode.where(title: episode['name'], episode: episode['number'], season: @season)
      if @episode.count == 0 and episode
        summary = episode['summary']
        if summary
          summary = summary[3..summary.size-5]
          image = episode['image']
          if image
            image = image['original']
          end
          @episode = Episode.new(title: episode['name'], summary: summary, episode: episode['number'], image: image, season: @season)
          @episode.save
        else
          @season.delete
          return render json: {success: true, id: @tv_show.id, title: @tv_show.title}
        end
      end
    end
    render json: {success: true, id: @tv_show.id, title: @tv_show.title}
  end

  private
  def tvshow_params
    params.require(:tvshow).permit(:title, :category)
  end
end
