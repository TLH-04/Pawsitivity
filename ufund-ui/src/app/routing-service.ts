import { UserServiceService } from './user-service.service';
import { Router, ActivatedRoute } from '@angular/router';
import { Injectable } from '@angular/core';
import { Usertype } from './user';

@Injectable({
  providedIn: 'root'
})
export class RoutingService {
  private requirePermission = new Map<string, Usertype[]>()
  constructor(private router: Router,private activatedRoute: ActivatedRoute, private userServiceService: UserServiceService) {
    this.requirePermission.set("_NeedEditor",[Usertype.ADMIN,Usertype.SUPER])
    this.requirePermission.set("_BasketComponent",[Usertype.HELPER])
    this.requirePermission.set("_MetricsDashboard",[Usertype.ADMIN,Usertype.SUPER])
  }

  /**
   * routes the user back to the login page
   */
  routeToLogin(){
    this.router.navigate(["./login"])
  }

  /**
   * Verifies the component page permission via verifyPage
   *
   * If the user is allowed on the current page then are moved to the selected route
   *
   * @param componentName current component user is on
   * @param route the page the user is moving to
   */
  routeToPage(componentName: string,route: string){
      if (this.verifyPage(componentName)){
        this.router.navigate([route])
      }
  }

  /**
   * Verifies the component page permission
   *
   * Gets the current usertype and verifies that they are both logged and able to reach this page
   * if they are not they are auto routed back to to login page
   *
   * @param componentName current component user is on
   * @returns if the user is allowed on the current component
   */
  verifyPage(componentName: string): boolean{
    if (!this.userServiceService.isloggedin()){
      this.router.navigate(['/login'])
      return false;
    }
    if (this.requirePermission.get(componentName)){
      var usertype: Usertype | undefined = this.userServiceService.getUserType()
      if ( ( !this.userServiceService.isloggedin() || usertype == undefined ) || ( !this.requirePermission.get(componentName)?.includes(usertype!)) ) {
        this.router.navigate(['/login'])
        return false;
      }
    }
    return true;
  }


}
