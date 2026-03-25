import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { LoginComponent } from './login/login.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { provideHttpClient } from '@angular/common/http';
import { CupboardModule } from './cupboard/cupboard-module';
import { NeedEditor } from './need-editor/need-editor';
import { BasketComponent } from './basket/basket.component';
import { NotFound } from './not-found/not-found';
import { AgChartsModule } from 'ag-charts-angular';
import { AgChartOptions } from 'ag-charts-community';
import { MetricsDashboard } from './metrics-dashboard/metrics-dashboard';
import { DatePipe } from '@angular/common';

import { Popup } from './popup/popup';
import { AdminChat } from './admin-chat/admin-chat';
import { CurrencyPipe } from '@angular/common';

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    NeedEditor,
    BasketComponent,
    NotFound,
    MetricsDashboard,
    Popup,
    AdminChat
  ],
  imports: [
    AgChartsModule,
    BrowserModule,
    AppRoutingModule,
    ReactiveFormsModule,
    CupboardModule,
    FormsModule,
  ],
  providers: [
    provideHttpClient(),
    DatePipe,
    CurrencyPipe
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
