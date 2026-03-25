import { UserServiceService } from './../user-service.service';
import { MessagingService } from './../messaging-service';
import { Component } from '@angular/core';
import { Need, Needtype } from '../need';
import { Helper, loginrequest, User, Usertype} from '../user';
import { Basket } from '../basket';
import { CupboardServiceService } from '../cupboard-service.service';
import { AgChartOptions, AgChartTheme } from "ag-charts-community";
import { ActivatedRoute } from '@angular/router';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { AdminChat } from '../admin-chat/admin-chat';
import { Popupservice } from '../popupservice';
import { RoutingService } from '../routing-service';

@Component({
  selector: 'app-metrics-dashboard',
  standalone: false,
  templateUrl: './metrics-dashboard.html',
  styleUrl: './metrics-dashboard.css',
})
export class MetricsDashboard {
  needs: Need[] = [];
  users: User[] = [];
  needOptions: AgChartOptions = {};
  listOptions: AgChartOptions = {};
  fillOptions: AgChartOptions = {};
  cumulativeOptions: AgChartOptions = {};
  userOptions: AgChartOptions = {};
  basketOptions: AgChartOptions = {};
  
  myTheme: AgChartTheme = {
    palette: {
        fills: ['#008395', '#FFADAD', '#B9DADC', '#FFDEDE', '#FFFFFF'],
        strokes: ['rgba(0, 50, 70, 1)', 'rgba(1, 44, 23, 1)', 'rgba(116, 58, 78, 1)', '#e7cacaff', 'rgba(2, 79, 109, 1)'],
    },
  };

  createform = new FormGroup({
    name: new FormControl("",[Validators.required,Validators.maxLength(256)]),
    password: new FormControl("",[Validators.required,Validators.maxLength(256 * 2)])
  })

  constructor(private popupService: Popupservice, private routingService: RoutingService,private messagingService: MessagingService,private cupboardService: CupboardServiceService, private userService: UserServiceService, private routeService: RoutingService, private activeroute: ActivatedRoute, private UserServiceService: UserServiceService) {}

  ngOnInit(): void {
    this.cupboardService.getNeeds().subscribe({

        next: (data) => {
          console.log('Needs loaded:', data);
          this.needs = data;

          this.needOptions = {
            title: {
              text: 'Needs By Type',
            },
            data: this.compileNeedTypeData(),
            series: [{ type: 'pie', angleKey: 'Amount', legendItemKey: 'NeedType'}],
            background: { fill: 'none', },
            theme: this.myTheme
          };

          this.listOptions = {
            title: {
              text: 'Need Visiblity'
            },
            data: this.compileNeedVisiblityData(),
            series: [{ type: 'pie', angleKey: 'Amount', legendItemKey: 'Visiblity'}],
            background: { fill: 'none', },
            theme: this.myTheme
          };

          this.fillOptions = {
            title: {
              text: 'Need Fufillment'
            },
            data: this.compileNeedFufillmentData(),
            series: [{ type: 'pie', angleKey: 'Amount', legendItemKey: 'Fufillment'}],
            background: { fill: 'none', },
            theme: this.myTheme,
          };

          this.cumulativeOptions = {
            title: {
              text: 'Needs Overview'
            },
            data: this.compileCumulativeData(),
            series: [
              { type: 'bar', xKey: 'NeedType', yKey: 'TypeAmount', stackGroup: 'type'},
              { type: 'bar', xKey: 'NeedType', yKey: 'Fufilled', stackGroup: 'subtype'},
              { type: 'bar', xKey: 'NeedType', yKey: 'Listed', stackGroup: 'subtype'},
            ],
            background: { fill: 'rgba(255, 255, 255, 0.47)',},
            theme: this.myTheme,
          }

        },
        error: (err) => {
          this.routingService.routeToLogin();
          console.error('Error loading needs:', err)
        }
    });

    this.userService.getAllUsers().subscribe({
      error: () => {
        this.routingService.routeToLogin();
      },
      next: (data) => {
        console.log('Users loaded:', data);
        this.users = data;

        this.userOptions = {
            title: {
              text: 'Users By Type',
            },
            data: this.compileUserMetricData(),
            series: [{ type: 'pie', angleKey: 'Amount', legendItemKey: 'UserType'}],
            background: { fill: 'none', },
            theme: this.myTheme
          };

        this.basketOptions = {
          title: {
            text: 'Number of Needs in Baskets',
          },
          data: this.compileUserBasketData(),
          series: [{ type: 'bar', xKey: 'items', yKey: 'needs', xName: 'Number of Needs', yName: 'Number of Users '}],
          background: { fill: 'none', },
          theme: this.myTheme,
          axes: [
            {
              type: "category",
              position: "bottom",
              title: { text: "Num Needs"}
            },
            {
              type: "number",
              position: "left",
              title: {
                text: "Num Users",
              },
            },
          ],
        };
      }
    });


    if (this.activeroute.component == this.constructor){
      this.routingService.verifyPage(this.activeroute.component.name)
    }

  }

  /**
   * Prompts userservice to create an admin
   * @param event button event
   */
  createAdmin(event: Event) {
    event.preventDefault();
    const request: loginrequest = {
      username: this.createform.get("name")!.value!.trim(),
      password: this.createform.get("password")!.value!.trim(),
    }

    this.userService.createAdmin(request,
    (response) =>{
        this.popupService.successPopup("Admin", "Admin created succesfully")
        this.createform.get("name")?.setValue("")
        this.createform.get("password")?.setValue("")
    },
    (error) => {


    }
    )
  }

  /**
   * verifies if the current user is a super admin
   * @returns if the user is a super admin
   */
  isSuperAdmin(): boolean {
    return this.userService.isUserType(Usertype.SUPER);
  }

  /**
   * Returns count of all needs currently in cupboard
   *
   * @returns number total needs currently in cupboard
   */
  getTotalNeeds(): number {
    return this.needs.length;
  }

  /**
   * Returns count of volunteer needs currently in cupboard
   *
   * @returns number total volunteer needs currently in cupboard
   */
  getTotalVolunteer(): Need[] {
    let vNeeds: Need[] = [];
    this.needs.forEach((need) => {
      if(need['need-type'] === Needtype.VOLUNTEER){
        vNeeds.push(need);
      }
    })

    return vNeeds;
  }

  /**
   * Returns count of resource needs currently in cupboard
   *
   * @returns number total resource needs currently in cupboard
   */
  getTotalResource(): Need[] {
    let rNeeds: Need[] = [];
    this.needs.forEach((need) => {
      if(need['need-type'] === Needtype.RESOURCE){
        rNeeds.push(need);
      }
    })

    return rNeeds;
  }

  /**
   * Returns count of monetary needs currently in cupboard
   *
   * @returns number total monetary needs currently in cupboard
   */
  getTotalMonetary(): Need[] {
    let mNeeds: Need[] = [];
    this.needs.forEach((need) => {
      if(need['need-type'] === Needtype.MONETARY){
        mNeeds.push(need);
      }
    })

    return mNeeds;
  }

   /**
   * Compiles the data for the needs by type metric
   *
   * @returns the data for the chart options component
   */
  compileNeedTypeData(): any[] {
    let data = [];
    data.push(
      {NeedType: 'Volunteer', Amount: this.getTotalVolunteer().length},
      {NeedType: 'Monetary', Amount: this.getTotalMonetary().length},
      {NeedType: 'Resource', Amount: this.getTotalResource().length}
    );
    return data;
  }


  /**
   * gets the amount of needs currently listed and delisted, then puts it into a data structure for the graph
   * @returns the data for the chart options component
   */
  compileNeedVisiblityData(): any[] {
    let listed: Need[] = [];
    let delisted: Need[] = [];
    this.needs.forEach((need) => {
      if(need['listed'] == true){
        listed.push(need);
      }
      else{
        delisted.push(need);
      }
    })

    let data = [];
    data.push(
      {Visiblity: 'Listed', Amount: listed.length},
      {Visiblity: 'Delisted', Amount: delisted.length}
    );

    return data;
  }

  /**
   * gets the amount of needs currently fufilled and unfufilled, then puts it into a data structure for the graph
   * @returns the data for the chart options component
   */
  compileNeedFufillmentData(): any[] {
    let fufilled: Need[] = [];
    let unfufilled: Need[] = [];
    this.needs.forEach((need) => {
      if(need['goal'] == need['progress']){
        fufilled.push(need);
      }
      else{
        unfufilled.push(need);
      }
    })

    let data = [];
    data.push(
      {Fufillment: 'Fufilled', Amount: fufilled.length},
      {Fufillment: 'Unfufilled', Amount: unfufilled.length}
    );

    return data;
  }

  compileCumulativeData(): any[] {
    let data = [];
    let volunteer = this.getTotalVolunteer();
    let monetary = this.getTotalMonetary();
    let resource = this.getTotalResource();

    let vListed: Need[] = [];
    let mListed: Need[] = [];
    let rListed: Need[] = [];

    let vFufilled: Need[] = [];
    let mFufilled: Need[] = [];
    let rFufilled: Need[] = [];
    let needs = [[volunteer, vListed, vFufilled], [monetary, mListed, mFufilled], [resource, rListed, rFufilled]];

    needs.forEach((needType) => {
      needType[0].forEach((need) => {
        if(need['listed'] == true){
          needType[1].push(need);
        }
          if(need['goal'] == need['progress']){
          needType[2].push(need);
        }
      })
    })

    data.push(
      {NeedType: 'Volunteer', TypeAmount: volunteer.length, Listed: vListed.length, Fufilled: vFufilled.length},
      {NeedType: 'Monetary', TypeAmount: monetary.length, Listed: mListed.length, Fufilled: mFufilled.length},
      {NeedType: 'Resource', TypeAmount: resource.length, Listed: rListed.length, Fufilled: rFufilled.length}
    );
    return data;
  }

  /**
   * Returns count of all users currently in system
   *
   * @returns number total users currently in system
   */
  getTotalUsers(): number {
    return this.users.length;
  }

  /**
   * Returns count of all admins currently in system
   *
   * @returns number total users currently in system
   */
  getTotalAdmin(): number {
    let admins = [];
    this.users.forEach((user) => {
      if(user['type'] === Usertype.ADMIN){
        admins.push(user);
      }
    });
      return admins.length;
  }

  //add super admin

  /**
   * Returns count of all helpers currently in system
   *
   * @returns number total helpers currently in system
   */
  getTotalHelper(): number {
    let admins = [];
    this.users.forEach((user) => {
      if(user['type'] === Usertype.HELPER){
        admins.push(user);
      }
    });
      return admins.length;
  }

  /**
   * Compiles the data for the users by type metric
   *
   * @returns the data for the chart options component
   */
  compileUserMetricData(): any[] {
    let data = [];
    data.push(
      {UserType: 'Super Admin', Amount: 1},
      {UserType: 'Admin', Amount: this.getTotalAdmin()},
      {UserType: 'Helper', Amount: this.getTotalHelper()}
    );
    return data;
  }


   /**
   * Goes through all helpers to find top contributor
   *
   * @returns the top helper as a user
   */
  getTopContibutor(): Helper {
    let baskt: Basket = {
      needsMap: {}
    }
    let failSafe: Helper = {
      name: "fake",
      password: "fake",
      type: Usertype.HELPER,
      basket: baskt,
      contributedNeeds: 0
    }

    let topHelper: User = this.users[0];
    let topContibution = 0;
    this.users.forEach(user =>{
      if(user.type == Usertype.HELPER)
      {
        if((user as Helper).contributedNeeds > topContibution){
          topHelper = user;
          topContibution = (user as Helper).contributedNeeds;
        }
      }

    });
    // console.log(topHelper);
    return topHelper != undefined ? topHelper.type == Usertype.HELPER ? (topHelper as Helper) : failSafe : topHelper;
  }

   /**
   * Compiles the data for the users by type metric
   *
   * @returns the data for the chart options component
   */
  compileUserBasketData(): any[] {
    let amount: number[] = [0,0,0,0,0];

    this.users.forEach(user => {
      if(user.type == Usertype.HELPER)
      {
        let needs = Object.keys((user as Helper).basket.needsMap).length;
        if(needs >= 5){
          amount[5] += 1;
        }
        else{
          amount[needs] += 1;
        }
      }
    })

    let data = [];
    data.push(
      {items: '0', needs: amount[0]},
      {items: '1', needs: amount[1]},
      {items: '2', needs: amount[2]},
      {items: '3', needs: amount[3]},
      {items: '4', needs: amount[4]},
      {items: '5+', needs: amount[5]},
    );
    return data;
  }


  /**
   * Sends user to different compotent
   * @param page the page the routeservice will attempt to move to.
   */
  pageTo(page: string): void {
    this.routeService.routeToPage(this.activeroute.component!.name,page)
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


}
