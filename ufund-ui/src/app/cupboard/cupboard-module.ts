import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CupboardComponent } from './cupboard.component';
import { CupboardSearchComponent } from '../cupboard-search/cupboard-search.component';
import { FormsModule, ɵInternalFormsSharedModule } from "@angular/forms";




@NgModule({
  declarations: [
    CupboardComponent,
    CupboardSearchComponent,
  ],
  imports: [
    CommonModule,
    FormsModule,
    ɵInternalFormsSharedModule
],
  exports: [
    CupboardComponent
  ]
})
export class CupboardModule { }
