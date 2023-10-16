import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HomeComponent } from './pages/home/home.component';
import { LoginComponent } from './pages/login/login.component';
import { NotFoundComponent } from './pages/not-found/not-found.component';
import { CreateGameComponent } from './pages/create-game/create-game.component';
import { ActiveGameComponent } from './pages/active-game/active-game.component';
import { PlayGameComponent } from './pages/play-game/play-game.component';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'rooms', component: ActiveGameComponent },
  { path: 'createroom', component: CreateGameComponent },
  { path: 'play', component: PlayGameComponent },
  { path: '', component: HomeComponent },

  { path: '**', component: NotFoundComponent },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  declarations: [
  ],
  exports: [RouterModule],
})
export class AppRoutingModule {}
