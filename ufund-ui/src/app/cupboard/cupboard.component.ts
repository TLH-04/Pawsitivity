import { Component } from '@angular/core';
import { Need } from '../need';
import { UserServiceService } from '../user-service.service';
import { ActivatedRoute, Router } from '@angular/router';
import { CupboardServiceService } from '../cupboard-service.service';
import { RoutingService } from '../routing-service';
import { Usertype } from '../user';

@Component({
    selector: 'app-cupboard',
    templateUrl: './cupboard.component.html',
    styleUrl: './cupboard.component.css',
    standalone: false
})
export class CupboardComponent {
  needs: Need[] = [];

  constructor(private routeService: RoutingService,private cupboardService: CupboardServiceService,private UserServiceService: UserServiceService,private activeroute: ActivatedRoute,private router: Router) {}

  /**
   * Verifys the user is logged in
   * @returns
   */
  isLoggedin(): boolean {
    return this.UserServiceService.isloggedin()
  }

  /**
   * Sends user to different compotent
   * @param page the page the routeservice will attempt to move to.
   */
  pageTo(page: string): void {
    this.routeService.routeToPage(this.activeroute.component!.name,page)
  }

  /**
  * Checks if the current user has helper permissions.
  *
  * @returns true if the current user is an Helper
  */
  isHelper(): boolean{
    return this.UserServiceService.isUserType(Usertype.HELPER);
  }

  /**
   * sends a request to userservice for to logout
   * @param event
   */
  logout(event: Event): void{
    event.preventDefault();
    this.UserServiceService.logout().subscribe({
      next: () => {
        this.routeService.routeToLogin()
      }
    })
  }

  /**
   * Updates the list of needs in the cupboard
   */
  getNeeds(): void {
      this.cupboardService.getNeeds().subscribe({
      next: data => this.needs = data,
    });
  }

  ngOnInit(): void {
    if (this.activeroute.component == this.constructor){
        this.routeService.verifyPage(this.activeroute.component.name)
    }
    this.cupboardService.getNeeds().subscribe({
        next: (data) => {
          console.log('Needs loaded:', data);
          this.needs = data;
        },
        error: (err) => console.error('Error loading needs:', err)
    });
  }


}
