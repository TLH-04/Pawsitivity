import { Component } from '@angular/core';
import { Popupservice } from '../popupservice';
import { PopupType } from '../PopupTypes';
import { MatDialog } from '@angular/material/dialog';

@Component({
  selector: 'app-popup',
  standalone: false,
  templateUrl: './popup.html',
  styleUrl: './popup.css'
})
export class Popup {
  constructor(private popupservice: Popupservice,public dialog: MatDialog) {}
  Types = PopupType;

  getMessage(): string {
    return this.popupservice.getMessage();
  }

  getPopType(): PopupType {
    return this.popupservice.getPopupType();
  }
  
  getSuccessType(): string {
    return this.popupservice.getSuccessType();
  }

  onConfirm(event: Event){
    event.preventDefault()
    this.dialog.closeAll();
    this.popupservice.onConfirm();
  }
  onCancel(event: Event){
    event.preventDefault()
    this.dialog.closeAll();
    this.popupservice.onCancel();
  }

  onClose(event: Event){
    event.preventDefault()
    this.dialog.closeAll();
  }
}
