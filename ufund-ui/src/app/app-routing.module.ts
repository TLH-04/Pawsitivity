import { NgModule, Component } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { CupboardComponent } from './cupboard/cupboard.component';
import { CupboardSearchComponent } from './cupboard-search/cupboard-search.component';
import { BasketComponent } from './basket/basket.component';
import { NeedEditor } from './need-editor/need-editor';
import { NotFound } from './not-found/not-found';
import { MetricsDashboard } from './metrics-dashboard/metrics-dashboard';


const routes: Routes = [
  {
    path: '',
    redirectTo: 'login',
    pathMatch: "full"
  },
  {
    path: 'login',
    component:LoginComponent
  },
  {
    path: 'need-creator',
    component: NeedEditor
  },
  {
    path: 'need-editor/:id',
    component: NeedEditor
  },
  {
    path: 'cupboard',
    component: CupboardComponent
  },
  {
    path: 'needs',
    component: CupboardSearchComponent
  },
  {
    path: 'basket',
    component: BasketComponent
  },
  {
    path: 'metrics-dashboard',
    component: MetricsDashboard
  },
  {
    path: '**',
    component: NotFound
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
