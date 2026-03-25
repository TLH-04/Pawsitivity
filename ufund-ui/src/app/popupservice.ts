import { inject, Injectable } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Popup } from './popup/popup';
import { PopupType } from './PopupTypes';
type Responsecallback = (response: void) => void;

@Injectable({
  providedIn: 'root'
})

export class Popupservice {
    constructor(){}

    private popupMessage = "";
    private successType = "Checkout"
    private popupType: PopupType = PopupType.CONFIRMATION;
    private onConfirmation: Responsecallback = () => 1;
    private onRejection: Responsecallback = () => 1;

    readonly dialog = inject(MatDialog)

    OpenConfirmationPopup(Message: string,onConfirm: Responsecallback,onCancel: Responsecallback): void{
        this.popupType = PopupType.CONFIRMATION;
        this.popupMessage = Message;
        this.onConfirmation = onConfirm;
        this.onRejection = onCancel;
        this.dialog.open(Popup,{
            panelClass: 'square-dialog'
        });
    }

    successPopup(successType: string, Message: string): void{
      this.popupType = PopupType.SUCCESS_MESSSAGE;
      this.successType = successType;
      this.popupMessage = Message;
      this.dialog.open(Popup,{
        panelClass: 'square-dialog'
      })
    }

    onCancel(): void{
      this.onRejection();
      this.resetPrompt();
    }

    onConfirm(): void{
      this.onConfirmation();
      this.resetPrompt();
    }

    resetPrompt() {
        this.onConfirmation = () => 1;
        this.onRejection = () => 1;
    }

    getMessage(): string {
      return this.popupMessage;
    }

    getPopupType(): PopupType {
      return this.popupType;
    }
    
    getSuccessType(): string {
      return this.successType;
    }

}
