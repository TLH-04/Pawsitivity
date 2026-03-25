import { Popupservice } from './../popupservice';
import { CupboardServiceService } from './../cupboard-service.service';
import { Need } from './../need';
import { Component } from '@angular/core';
import { FormControl, FormGroup, ValidatorFn, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Needtype } from '../need';
import { HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { RoutingService } from '../routing-service';
import { CurrencyPipe, formatCurrency } from '@angular/common';

@Component({
  selector: 'app-need-editor',
  standalone: false,
  templateUrl: './need-editor.html',
  styleUrl: './need-editor.css'
})
export class NeedEditor {
  private CREATEURL = "/need-creator"
  createMode = false;
  needId = 0;
  needtypes = Object.values(Needtype)
  booleanoptions = [true,false]
  goalPlaceholder = '0'
  progressPlaceholder = '0'
  isMonetary = false;

  needForm = new FormGroup(
  {
    name: new FormControl('', [Validators.required, Validators.maxLength(256)]),
    desc: new FormControl('', Validators.required),
    goal: new FormControl('', [Validators.required, Validators.min(1),Validators.max(2147483647),Validators.pattern(/^(?!.*[eE]).*$/)]),
    progress: new FormControl('', [Validators.required, Validators.min(0),Validators.max(2147483647),Validators.pattern(/^(?!.*[eE]).*$/)]),
    type: new FormControl(Needtype.RESOURCE),
    listed: new FormControl(true)
  },
  { validators: this.checkform() }
  );


  checkform(): ValidatorFn {
    return (control) => {
      const form = control as FormGroup;
      const progress = Number(form.get('progress')?.value);
      const goal = Number(form.get('goal')?.value);
      const errors = form.get('progress')?.errors || {}
      if (progress > goal) {
        form.get('progress')?.setErrors({ greaterThanGoal: true });
        return { greaterThanGoal: true };
      } else {
        if ('greaterThanGoal' in errors) {
          delete errors['greaterThanGoal'];
          form.get('progress')?.setErrors(Object.keys(errors).length ? errors : null);
        }
      }


      return null;
    }
  }

  constructor(private popupservice: Popupservice,private routeService: RoutingService,private cupboardService: CupboardServiceService,private router: Router,private activeroute: ActivatedRoute, private currencyPipe: CurrencyPipe) {}

  ngOnInit(): void{

      if (this.activeroute.component == this.constructor){
        this.routeService.verifyPage(this.activeroute.component.name)
      }

      if (this.router.url == this.CREATEURL){
        this.createMode = true;
        this.needId = 0;
      } else {
        var needId = Number(this.activeroute.snapshot.paramMap.get('id'))
        this.needId = needId;
        this.createMode = false;


        this.cupboardService.searchNeedId(this.needId).subscribe(
          (result: Need) => {
            this.needForm.patchValue({
              name: result.name,
              desc: result.description,
              type: result['need-type'],
              progress: String(result.progress),
              goal: String(result.goal),
              listed: Boolean(result.listed)
            })
          }
        )
      }
  }


  /**
   * verifies if user inputs are valid.
   * @returns the valid status of the need form
   */
  isValid(): boolean {
    return !this.needForm.invalid
  }

  /**
  * Deletes the current need from the cupboard.
  *
  * sends a delete request to the server through the CupboardService.
  * After deletion, the user is ent back to the cupboard view.
  *
  * @param event The click
  */
  onDelete(event: Event): void{
    event.preventDefault();
    var dialogText = "Are you sure you want to delete this need?"
    this.popupservice.OpenConfirmationPopup(dialogText,() => {
      if (this.createMode == false){
        this.cupboardService.deleteNeed(this.needId).subscribe({
          next: () => {

          }
        })
        this.routeService.routeToPage(this.activeroute.component!.name,"/cupboard")
      }
    },()=>1);
  }

  /**
  * Handles the form submission for creating or editing a need.
  *
  * in creation or edit mode, it sends the appropriate
  * HTTP request via the CupboardService. afterwards,
  * the user is paged back to the cupboard page.
  *
  * @param event The submit event
  */
  onSumbit(event: Event): void {
    event.preventDefault()
    var selectedtype: Needtype = this.needForm.get("type")!.value as Needtype
    var need: Need = {
      name: String(this.needForm.get("name")?.value),
      description: String(this.needForm.get("desc")?.value),
      progress: Number(this.needForm.get("progress")?.value),
      goal: Number(this.needForm.get("goal")?.value),
      'need-type': selectedtype,
      id: this.needId,
      listed: Boolean(this.needForm.get("listed")?.value)
    }

    console.log(this.needForm.get("listed"))
    console.log(need);

    if (this.createMode == true){
      this.cupboardService.postNeed(need).subscribe({
        next: (response: Need) => {
          this.popupservice.successPopup("Create", "Need created succesfully");
          this.routeService.routeToPage(this.activeroute.component!.name,"/cupboard")
        }
      });
    } else {
      this.cupboardService.editNeed(need).subscribe({
        next: (response: Need) => {
          this.routeService.routeToPage(this.activeroute.component!.name,"/cupboard")
        }
      });
    }

  }

  /**
   * Sends user to different compotent
   * @param page the page the routeservice will attempt to move to.
   */
  pageTo(page: string): void {
    this.routeService.routeToPage(this.activeroute.component!.name,page)
  }

  /**
   * Checks the type of need being created, and bases placeholder text & input formating on it.
   */
  checkType(event: Event): void {
    event.preventDefault()

    let selectedType = (event.target as HTMLSelectElement).value

    if(selectedType.toLowerCase() === 'monetary'){
      this.isMonetary = true
      this.goalPlaceholder = '$0.00'
      this.progressPlaceholder = '$0.00'

    } else {
      this.isMonetary = false
      this.goalPlaceholder = '0'
      this.progressPlaceholder = '0'
    }

    console.log("Selected Monetary: ", this.isMonetary)
  }
}
