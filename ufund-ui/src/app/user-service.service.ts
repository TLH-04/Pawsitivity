import { HttpClient, HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { CookieService } from 'ngx-cookie-service';
import { ActivatedRoute, Router } from '@angular/router';
import { Usertype, loginrequest, User } from './user';
import { Observable } from 'rxjs';

type Responsecallback = (response: HttpResponse<any>) => void;
type errorcallback = (error: HttpErrorResponse) => void;

@Injectable({
  providedIn: 'root'
})

export class UserServiceService {
  private requestUrl = 'http://localhost:8080/login'
  private createUrl = 'http://localhost:8080/users/create'
  private getURL = 'http://localhost:8080/users/all'
  private readonly STORED_USERNAME = 'ufund-username';
  username: string = '';

  login(loginrequest: loginrequest, onResponse: Responsecallback,OnError: errorcallback){
    console.log("Got login request " + loginrequest)
    this.username = loginrequest.username;
    localStorage.setItem(this.STORED_USERNAME, this.username);
    console.log(this.username)
    this.http.post<any>(this.requestUrl,loginrequest,{ observe: 'response', withCredentials: true  }).subscribe({
      next: (response: HttpResponse<any>) => {
        onResponse(response)
      },
      error: (error: HttpErrorResponse) => {
        OnError(error)
      }
    })
  }

  getUserName(): string {
    if (!this.username) {
      const stored = localStorage.getItem(this.STORED_USERNAME);
      this.username = stored ?? '';
    }
    return this.username;
  }

  createHelper(loginrequest: loginrequest, onResponse: Responsecallback,OnError: errorcallback){
    console.log("Got login request " + loginrequest)
    this.http.post(this.createUrl + "/helper",loginrequest,{ responseType: 'text', observe: 'response', withCredentials: true,   }).subscribe({
      next: (response: HttpResponse<any>) => {
        onResponse(response)
      },
      error: (error: HttpErrorResponse) => {
        OnError(error)
      }
    })
  }

  createAdmin(loginrequest: loginrequest, onResponse: Responsecallback,OnError: errorcallback){
    console.log("Got login request " + loginrequest)
    this.http.post(this.createUrl + "/admin",loginrequest,{ responseType: 'text', observe: 'response', withCredentials: true,   }).subscribe({
      next: (response: HttpResponse<any>) => {
        onResponse(response)
      },
      error: (error: HttpErrorResponse) => {
        OnError(error)
      }
    })
  }

  logout(){
    console.log("Got logout request")
    return this.http.get<any>(this.requestUrl,{withCredentials: true})
  }

  isloggedin(): boolean {
    const hasusercookie: boolean = this.cookieService.check('SessionId');
    return hasusercookie
  }

  isUserType(type: Usertype): boolean {
    if (this.cookieService.check('Permission')) {
        return this.cookieService.get('Permission') == type || (type == Usertype.ADMIN && this.cookieService.get('Permission') == Usertype.SUPER)
    }
    return false
  }

  getUserType(): Usertype | undefined {
    if (this.cookieService.check('Permission')) {
        return Usertype[this.cookieService.get('Permission') as keyof typeof Usertype]
    }
    return undefined
  }

  getAllUsers(): Observable<User[]> {
    return this.http.get<User[]>(this.getURL);
  }

  routeToPage(){ // IF THEY ARENT LOGGED IN GO TO LOGIN PAGE
    if (!this.isloggedin()){
       this.router.navigate(['/login'])
    }
  }

  constructor(private http: HttpClient,private cookieService: CookieService,private router: Router) {

  }
}
