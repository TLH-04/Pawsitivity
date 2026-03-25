import { Component } from '@angular/core';
import { UserServiceService } from './user-service.service';
import { ActivatedRoute, Router } from '@angular/router';
import { RoutingService } from './routing-service';

@Component({
    selector: 'app-root',
    templateUrl: './app.component.html',
    styleUrl: './app.component.css',
    standalone: false
})
export class AppComponent {
  /**
   * verifies the user is logged in
   * @returns boolean
   */
  isLoggedin(): boolean {
    return this.UserServiceService.isloggedin()
  }


  title = 'U-Fund Management';


  constructor(private routeService: RoutingService,private UserServiceService: UserServiceService, private router: Router, private activeroute: ActivatedRoute){};
  
}
