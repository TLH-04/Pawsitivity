import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import { Need } from './need';
import { BasketRequest } from './basket-request';
import { Basket } from './basket';
import { RoutingService } from './routing-service';
import { Messageservice } from './messageservice';

//do we javadoc angular?

@Injectable({ providedIn: 'root' })
export class BasketService {

  private basketURL = 'http://localhost:8080/basket'

  httpOptions = {
    headers : new HttpHeaders({ 'Content-Type': 'application/json'}),
    withCredentials : true
  };

  constructor(
    private http: HttpClient,
    private messageService: Messageservice
  ) { }

  /** GET : view the current basket for a user */
  getUserBasket(): Observable<Basket> {
    return this.http.get<Basket>(this.basketURL, this.httpOptions)
    .pipe(
      catchError(this.messageService.handleError<Basket>('getNeeds'))
    )
  }

  /** POST : add a need to a user's basket*/
  addNeed(request : BasketRequest): Observable<Need> {
    const url = `${this.basketURL + "/need"}`;
    return this.http.post<Need>(url, request, {withCredentials : true}).pipe(
      catchError(this.messageService.handleError<Need>('addNeed'))
    );
  }

  /** PUT : remove some/all a need from a user's basket */
  removeNeed(request : BasketRequest): Observable<Need> {
    const url = `${this.basketURL + "/need"}`;
    return this.http.put<Need>(url, request, {withCredentials : true}).pipe(
      catchError(this.messageService.handleError<Need>('removed some/all of need from basket'))
    );
  }

  /** POST : checkout on user basket */
  checkout(): Observable<void> {
    const url = `${this.basketURL + "/checkout"}`;
    return this.http.post<void>(url, null , {withCredentials : true}).pipe(
      catchError(this.messageService.handleError<void>('checkout'))
    );
  }




}
