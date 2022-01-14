import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HomePageComponent } from './components/home/home-page/home-page.component';
import { SupportPageComponent } from './components/supportpage/support-page/support-page.component';



const routes: Routes = [

  {path:"", component: HomePageComponent},
  {path:"support", component: SupportPageComponent},
  

];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
