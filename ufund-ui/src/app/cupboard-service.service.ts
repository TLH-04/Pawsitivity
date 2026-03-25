import { catchError } from 'rxjs/operators';
import { Messageservice } from './messageservice';
import { UserServiceService } from './user-service.service';
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams, HttpResponse } from '@angular/common/http';
import { Need } from './need';
import { Observable } from 'rxjs';
@Injectable({
  providedIn: 'root'
})
export class CupboardServiceService {



  private cupboardUrl = 'http://localhost:8080/cupboard';



  httpOptions = {
    headers: new HttpHeaders({ 'Content-Type': 'application/json' }),
    withCredentials: true,
  };

 constructor(private http: HttpClient,private Messageservice: Messageservice) { }

 /**
 * Creates a new need in the cupboard.
 *
 * Sends a POST request to the backend API to create a new need.
 * The server clones the provided Need object and stores it.
 *
 * @param need The Need object to create.
 * @returns An Observable of type Need
 */
  postNeed(need: Need): Observable<Need>{
    return this.http.post<Need>(this.cupboardUrl + '/create',need,this.httpOptions).pipe(
      catchError(this.Messageservice.handleError<Need>('createNeed'))
    )
  }




  /**
  * Updates an existing need in the cupboard.
  *
  * Sends a PUT request with the modified Need object to update its properties.
  *
  * @param need The Need object to create.
  * @returns An Observable of type Need
  */
  editNeed(need: Need): Observable<Need>{
    return this.http.put<Need>(this.cupboardUrl + '/needs/update', need, this.httpOptions).pipe(
      catchError(this.Messageservice.handleError<Need>('editNeed'))
    )
  }




  /**
  * Retrieves all needs from the cupboard.
  *
  * Sends a GET request to get all needs currently stored in the cupboard.
  * @returns An Observable of type Need[]
  */
  getNeeds(): Observable<Need[]> {
    return this.http.get<Need[]>(this.cupboardUrl + "/needs", this.httpOptions).pipe(
      catchError(this.Messageservice.handleError<Need[]>('getsNeeds'))
    )
  }




  /**
  * Searches for needs by name.
  *
  * Sends a GET request with a query parameter to find all needs
  * whose names include the search term.
  * @param term string to search with
  * @returns Observable<Need[]>
  */
  searchNeeds(term: string): Observable<Need[]> {
    let query = new HttpParams();
    query = query.set("name",term)

    var httpOptions  = {
      headers: new HttpHeaders({ 'Content-Type': 'application/json' }),
      withCredentials: true,
      params: query
    };

    return this.http.get<Need[]>(this.cupboardUrl + "/needs/search",httpOptions).pipe(
      catchError(this.Messageservice.handleError<Need[]>('searchNeeds'))
    )
  }




  /**
  * Retrieves a single need by its ID.
  *
  * Sends a GET request to fetch a need that ID matches the provided ID.
  * @param id id to search with
  * @returns Observable<Need>
  */
  searchNeedId(id: number): Observable<Need> {
    return this.http.get<Need>(this.cupboardUrl + "/needs/id=" + id,this.httpOptions).pipe(
      catchError(this.Messageservice.handleError<Need>('searchNeedsID'))
    )
  }




  /**
  * Retrieves the listing status of a single need by its ID.
  *
  * Sends a GET request to fetch a listing status of need that ID matches the provided ID.
  * @param id id to search with
  * @returns Observable<Boolean>
  */
  needListingStatus(id: number): Observable<Boolean> {
    console.log("GETTING NEED LISTING STATUS REQUEST")
    return this.http.get<Boolean>(this.cupboardUrl + "/needs/listed/id=" + id,this.httpOptions).pipe(
      catchError(this.Messageservice.handleError<Boolean>('needListingStatus'))
    )
  }



  /**
  * Updates an existing need's listing status in the cupboard.
  *
  * Sends a PUT request with the modified Need object to update its properties.
  *
  * @param need The Need object to create.
  * @returns An Observable of type Need
  */
  editListingStatus(need: Need): Observable<Need>{
    console.log("Editing Need Status")
    return this.http.put<Need>(this.cupboardUrl + '/needs/listing', need, this.httpOptions).pipe(
      catchError(this.Messageservice.handleError<Need>('editNeed'))
    )
  }




  /**
  * Deletes a single need by its ID.
  *
  * Sends a Delete request to delete a need that ID matches the provided ID.
  * @param id id to search with
  * @returns Observable<Need>
  */
  deleteNeed(id: number): Observable<Need> {
    console.log("SENDING REQUEST")
    return this.http.delete<Need>(this.cupboardUrl + "/remove/id=" + id,this.httpOptions).pipe(
      catchError(this.Messageservice.handleError<Need>('searchNeedsID'))
    )
  }
}
