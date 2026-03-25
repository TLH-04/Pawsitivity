import { Popupservice } from './../popupservice';
import { BasketService } from './../basket.service';
import { RoutingService } from './../routing-service';
import { CupboardServiceService } from './../cupboard-service.service';
import { UserServiceService } from './../user-service.service';
import { Component } from '@angular/core';
import { Need } from '../need';
import { ActivatedRoute } from '@angular/router';
import { Basket } from '../basket';

@Component({
  selector: 'app-basket',
  standalone: false,
  templateUrl: './basket.component.html',
  styleUrl: './basket.component.css'
})

export class BasketComponent {
  needs: Map<string,Need> = new Map<string,Need>;
  basket: Basket = { needsMap: {} };
  selectedQuantity: {[id : number]: number} = {};


  constructor(private popupservice: Popupservice,private basketService: BasketService,private activeroute: ActivatedRoute,private cupboardService: CupboardServiceService, private routingService: RoutingService, private UserServiceService: UserServiceService){

  }

  ngOnInit(): void {
    if (this.activeroute.component == this.constructor){
        this.routingService.verifyPage(this.activeroute.component.name)
    }
    this.updateNeeds()
    this.updateBasket()
  }

  /**
   *
   *
   * Routes to cupboard
   */
  routeTocupboard(event: Event): void {
    event.preventDefault()
    this.routingService.routeToPage(this.activeroute.component!.name,"/cupboard")
  }

  /**
   * Sends user to different compotent
   * @param page the page the routeservice will attempt to move to.
   */
  pageTo(page: string): void {
    this.routingService.routeToPage(this.activeroute.component!.name,page)
  }


  /**
   * Makes a request to checkout via basket service,
   *
   * Routes to cupboard on sucesss
   */
  checkout(event: Event): void {
    event.preventDefault()
    this.basketService.checkout().subscribe({
      next: () => {
        this.popupservice.successPopup("Checkout", "Thank you for checking out!")
        this.routingService.routeToPage(this.activeroute.component!.name,"/cupboard")
      }
    })
  }

  /**
   * Sees if a user can checkout
   *
   */
  canCheckout(): boolean {
    return Object.keys(this.basket.needsMap).length > 0
  }

  /**
  * Updates the selected quantity for a given need.
  *
  *
  * @param value The selected quantity value.
  * @param need The Need whose quantity is being updated.
  */
  selectQuantity(event: Event,need: Need): void{
    var basketLimit = need.goal - need.progress;
    var value = Number((event.target as HTMLInputElement).value)

    if (value > 0 &&  value > basketLimit){
      value = basketLimit;
    } else if (value < 0 && value < -basketLimit) {
      value = -basketLimit;
    }

    this.selectedQuantity[need.id] = value
  }

  getQuantity(need: Need) {
    return this.selectedQuantity[need.id] ?? 0;
  }

  /**
   * Prompts the user if they are amount to remove a need, otherwise runs a function to send a request tob asket
   *
   *
   * @param event button event
   * @param need the need being edited in basket
   * @param changeby the amount you are adding to subtracting from the basket
   */
  editBasket(remove: boolean, event: Event,need: Need,changeby: number) {
    event.preventDefault()
    if (remove){
        const message = "Are you sure you want to remove this need?"
        this.popupservice.OpenConfirmationPopup(message,()=>{
          this.changeBasket(need,changeby)
        },()=>{ })

    } else {
        this.changeBasket(need,changeby)
    }
    
  }

    /**
   * Sumbits a request via basketService to add or remove needs
   *
   *
   * @param need the need being edited in basket
   * @param changeby the amount you are adding to subtracting from the basket
   */
  private changeBasket(need: Need,changeby: number): void{
    (changeby > 0 ? this.basketService.addNeed({id : need.id, amount: changeby}) : this.basketService.removeNeed({id : need.id, amount: changeby * -1})).subscribe({
      next: () => {
        this.updateNeeds()
        this.updateBasket()
      }
    })
  }

  /**
   * Updates the basket
   */
  updateBasket(): void {
    this.basketService.getUserBasket().subscribe({
      next: (data) => {
        console.log('Basket loaded:', data);
        for (let key in data.needsMap) {
            if (!this.selectedQuantity[key]){
                this.selectedQuantity[key] = 0;
            }
        }
        this.basket = data;
      },
    });
  }

  /**
   * Updates the list of needs
   */
  updateNeeds(): void {
      this.cupboardService.getNeeds().subscribe({
      next: (data) => {
        for (const need of data){
          this.needs.set(String(need.id),need)
        }
      },
    });
  }

  /**
   * sends a request to userservice for to logout
   * @param event
   */
  logout(event: Event): void{
    event.preventDefault();
    this.UserServiceService.logout().subscribe({
      next: () => {
        this.routingService.routeToLogin()
      }
    })
  }

}
