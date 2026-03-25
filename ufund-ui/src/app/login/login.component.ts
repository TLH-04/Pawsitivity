import { HttpErrorResponse } from '@angular/common/http';
import { UserServiceService } from './../user-service.service';

import { Component } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { RoutingService } from '../routing-service';

@Component({
  selector: 'app-login',
  standalone: false,
  templateUrl: './login.html',
  styleUrl: './login.css'
})
export class LoginComponent {
  protected username = new FormControl('',[Validators.required,Validators.maxLength(256)])
  protected password = new FormControl('',Validators.required)
  protected createaccount = false
  protected errormessage = '';
  protected systemmessage = ''


  /**
   * verifies the user is logged in
   * @returns boolean
   */
  isLoggedin(): boolean {
    return this.UserServiceService.isloggedin()
  }

  constructor(private routeService: RoutingService,private UserServiceService: UserServiceService, private router: Router,private activeroute: ActivatedRoute) {}



  ngOnInit() {

    if (this.activeroute.component == this.constructor){
      if (this.isLoggedin()){
        this.routeService.routeToPage(this.activeroute.component.name,"/cupboard")
      }
    }
  }

  /**
   * Switches login page to the create account page and vice versa
   */
  switchtype(){
    this.errormessage = ""
    this.createaccount = !this.createaccount
    if (this.createaccount == true){
      this.systemmessage = ""
    }
  }


  /**
   * attempts to create and account or login
   *
   * Calls userservice to send a request to the backend api
   *
   * @param event the button event
   */
  login(event : Event){
    event.preventDefault();
    console.log("attempted login " + this.username.value)

    if (this.createaccount == true){
      this.UserServiceService.createHelper({
        username: String(this.username.value),
        password: String(this.password.value),
      },
      (response) =>{
          console.log("GOT RESPONSE",response.status,response.body)

          if (response.status == 201){
            this.systemmessage = "Account created"
            this.createaccount = false
          }

      },
      (error) => {

          console.log("GOT ERROR " + error.status + " " + error.message)
          this.errormessage = "This username is already in use"

      }
      )
    } else {

      this.UserServiceService.login({
        username: String(this.username.value),
        password: String(this.password.value),
      },

      (response) =>{

          console.log("GOT RESPONSE",response)
          console.log(response.status,response.body)

          if (this.UserServiceService.isloggedin()){

            this.routeService.routeToPage(this.activeroute.component!.name,"/cupboard")
          }

      },
      (error) => {

          console.log("GOT ERROR " + error.status + " " + error.message)
          this.errormessage = "Please enter a valid username and password"

      }
      )
    }

  }
}
