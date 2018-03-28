Rails.application.routes.draw do
  # For details on the DSL available within this file, see http://guides.rubyonrails.org/routing.html
  resources :tvshows, only: [:index, :show, :create] do
    resources :seasons, only: [:index, :show] do
      resources :episodes, only: [:index, :show]
    end
  end
end
