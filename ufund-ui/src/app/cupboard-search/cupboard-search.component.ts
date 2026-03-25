import { Router } from '@angular/router';
import { Component, OnInit } from '@angular/core';
import { Observable, Subject } from 'rxjs';

import { Need } from '../need';
import { CupboardServiceService } from '../cupboard-service.service';
import { UserServiceService } from '../user-service.service';
import { Usertype } from '../user';
import { FormControl, Validators } from '@angular/forms';
import { Basket } from '../basket';
import { BasketService } from '../basket.service';



@Component({
  selector: 'app-cupboard-search',
  templateUrl: './cupboard-search.component.html',
  standalone: false,
  styleUrls: ['./cupboard-search.component.css']
})


export class CupboardSearchComponent {
  needs: Need[] = [];
  listedNeeds: Need[] = [];
  delistedNeeds: Need[] = [];
  searchPerformed = false;
  filterUnlisted = false;
  private searchTerms = new Subject<string>();
  basket: Basket = { needsMap: {} };

  needQuantity: {[id : number]: number} = {};
  remainingNeed = new Map<number,{save: string, array: number[]}>();



  constructor(private basketService: BasketService,private router: Router,private cupboardService: CupboardServiceService,private userService: UserServiceService) {}

  /**
  * Checks if the current user has admin permissions.
  *
  * @returns true if the current user is an Admin
  */
  isAdmin(): boolean{
    return this.userService.isUserType(Usertype.ADMIN);
  }




  /**
  * Updates the basket
  */
  updateBasket(): void {
    if (this.isHelper()){
      this.basketService.getUserBasket().subscribe({
        next: (data) => {
          console.log('Basket loaded:', data);
          this.basket = data;
        },
      });
    }
  }




  /**
  * Checks if the current user has helper permissions.
  *
  * @returns true if the current user is an Helper
  */
  isHelper(): boolean{
    return this.userService.isUserType(Usertype.HELPER);
  }




  /**
  * Routes to the need editor page for the specified need.
  * /need-editor/:id
  *
  * @param event The click event.
  * @param need The Need to edit.
  */
  editNeed(event: Event,need: Need): void{
    event.preventDefault();
    this.router.navigate(['need-editor/' + String(need.id)])
  }




  /**
  * Performs a search for needs matching the given term.
  *
  *
  * @param event The form submission or input event.
  * @param term The search term entered by the user.
  */
  search(event: Event, term: string): void {
    event.preventDefault()
    if (term.trim()) { // Enter Detected
      this.searchPerformed = true;
      this.cupboardService.searchNeeds(term).subscribe({
        next: (needs: Need[]) => {
          this.needs.splice(0, this.needs.length, ...needs);

          this.filterNeeds(needs)
        }
      })
      this.updateBasket()
    }
  }




  /**
  * Updates the selected quantity for a given need.
  *
  *
  * @param event The change event of the input box
  * @param need The Need whose quantity is being updated.
  */
  selectQuantity(event: Event,need: Need): void{
    console.log((need.goal - (need.progress + (this.basket.needsMap[need.id] ?? 0))))
    var value = Number((event.target as HTMLInputElement).value);
    if (value == null || value <= 0){
        value = 0;
    }
    if (value > (need.goal - (need.progress + (this.basket.needsMap[need.id] ?? 0)  ))) {
        value = (need.goal - (need.progress + (this.basket.needsMap[need.id] ?? 0)));
    }
    this.needQuantity[need.id] = value
  }

  /**
   * Checks if a need quantity can be modifed in the basket
   * @param need the need to check
   * @returns weather the need can be modfied
   */
  canModify(need: Need): boolean {
      return (need.goal - (need.progress + (this.basket.needsMap[need.id] ?? 0))) > 0;
  }

  /**
  * gets the selected quantity for a given need.
  *
  * @param need The Need whose quantity is being gottend.
  */
  getQuantity(need: Need): number{
    return this.needQuantity[need.id];
  }

  addToBasket(event: Event,need: Need): void {
    event.preventDefault()
    if (this.needQuantity[need.id] == null || this.needQuantity[need.id] <= 0){
      return
    }
    this.basketService.addNeed({id: need.id,amount: this.needQuantity[need.id]}).subscribe({
      next: () => {
        this.updateBasket()
        this.initializeNeeds()

      }
    })
  }




  /**
  * Generates an array of with the remaning quantity of a need
  *
  * The array ranges from 1 up to the remaining quantity
  *
  * @param need The Need to calculate available amount for.
  * @returns An array of integers representing selectable amount.
  */
  quantity(need: Need): number[] {
    var basketLimit = 0
    if (this.isHelper()) {
      basketLimit = need.goal - need.progress;
      if (this.basket.needsMap[need.id] !== undefined) {
          basketLimit = basketLimit - this.basket.needsMap[need.id] < 0 ? 0 : basketLimit - this.basket.needsMap[need.id]
      }
    }

    var save = need.id + "|" + basketLimit
    var item = this.remainingNeed.get(need.id)

    if (item && item.save === save) {
      return item.array;
    }

    this.remainingNeed.set(need.id,{array: Array.from({length: basketLimit}, (_, i) => i + 1),save: save})
    return this.remainingNeed.get(need.id)!.array

  }




  /**
  * returns the remaning quantity of a Need
  *
  * @param need The Need to calculate available amount for.
  * @returns An number selectable amount.
  */
  quantityL(need: Need): number {
    var basketLimit = 0
    if (this.isHelper()) {
      basketLimit = need.goal - need.progress;
      if (this.basket.needsMap[need.id] !== undefined) {
          basketLimit = basketLimit - this.basket.needsMap[need.id] < 0 ? 0 : basketLimit - this.basket.needsMap[need.id]
      }
    }
    if (basketLimit == 0) {
      return basketLimit
    }
    return basketLimit
  }




  /**
   * removes a fulfilled need from the cupboard's list
   *
   * @param need Need currently being checked for fulfillment status
   * @returns True: fullfilled
   * @returns False: unfullfilled
   */
  autoDelist(need: Need): boolean{
    var remaning = need.goal - need.progress

    if (remaning == 0 && !this.delistedNeeds.some(n => n.id === need.id)){
      this.delistedNeeds.push(need)
      var index = this.listedNeeds.findIndex((n: Need) => n.id === need.id)
      this.listedNeeds.splice(index, 1)
    }

    return remaning == 0
  }




  /**
   * removes a fulfilled need from the cupboard's list
   *
   * @param need Need currently being checked for fulfillment status
   * @returns True: fullfilled
   * @returns False: unfullfilled
   */
  manualDelist(need: Need): void{
    console.log("MANUALLY DELISTING NEED")

    this.cupboardService.editListingStatus(need).subscribe(updatedNeed => {
      console.log("Listing Status Updated:", updatedNeed)

      const index = this.listedNeeds.findIndex((n: Need) => n.id === updatedNeed.id)
      if(index !== -1){
        console.log("Need found at index ", index)
        this.listedNeeds.splice(index, 1)

        if(!this.delistedNeeds.some(n => n.id === updatedNeed.id)){
          this.delistedNeeds.push(updatedNeed)
        }
      }

    })
  }



  /**
   * Toggles filter flag for displaying delisted needs
   */
  displayUnlisted(): void{
    this.filterUnlisted = !this.filterUnlisted
    console.log("detected || current filtering status: ", this.filterUnlisted)
  }



  /**
   * Updates need's listing status to be true (active)
   *
   * @param need Need currently relisted
   */
  relistNeed(need: Need): void{
    console.log("MANUALLY RELISTING NEED")

    this.cupboardService.editListingStatus(need).subscribe(updatedNeed => {
      console.log("Listing Status Updated:", updatedNeed)

      const index = this.delistedNeeds.findIndex((n: Need) => n.id === updatedNeed.id)
      if(index !== -1){
        console.log("Need found at index ", index)
        this.delistedNeeds.splice(index, 1)
        this.listedNeeds.push(updatedNeed)
      }

    })
  }



  /**
   * Filters needs into two seperate arrays based on if they're currently listed
   * or unlisted.
   *
   * @param stored grouping of needs to be filtered
   */
  filterNeeds(stored: Need[]): void {
    this.delistedNeeds = []
    this.listedNeeds = []

    for(let need of stored){
      if(need.listed){
        this.listedNeeds.push(need)

      } else {
        this.delistedNeeds.push(need)
      }
    }
  }




  /**
  * Resets the cupboard view to show all needs.
  */
  goBack():void {
    this.updateBasket()
    if(this.searchPerformed){
      this.searchPerformed = false
    }
    this.initializeNeeds()
  }



  /**
  * Gets all needs currently stored within cupboard. Then splits them into
  * different arrays based on their listing status.
  */
  initializeNeeds(): void {
    this.cupboardService.getNeeds().subscribe({
          next: (needs: Need[]) => {
            for (const need of needs){
              this.needQuantity[need.id] = 0;
            }
            console.log(this.needs)
            console.log(needs)
            this.needs.splice(0, this.needs.length, ...needs);

            this.filterNeeds(needs)
          }
    })
  }




  ngOnInit(): void {
    this.initializeNeeds()
    this.updateBasket()
  }
}
