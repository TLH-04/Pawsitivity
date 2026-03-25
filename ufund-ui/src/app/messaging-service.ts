import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Message } from './message';
import { Messageservice } from './messageservice';
import { catchError, map } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class MessagingService {
  constructor(private http: HttpClient,private Messageservice: Messageservice){}

  private baseurl = "http://localhost:8080/chat"
  httpOptions  = {
      headers: new HttpHeaders({ 'Content-Type': 'application/json' }),
      withCredentials: true,
  };

  /**
   * sends a message to the server
   * @param message the content of the message
   */
  sendMessage(message: string){
    return this.http.post(this.baseurl + "/messages",message,this.httpOptions).pipe()
  }

  /**
   * Gets all messages in the admin chat up to a limit
   * @param limit the number of messages to get
   * @returns the most recent messages up to the limit
   */
  getMessages(limit: number) {
    let query = new HttpParams();
    query = query.set("limit",limit)

    var httpOptions  = {
      headers: new HttpHeaders({ 'Content-Type': 'application/json' }),
      withCredentials: true,
      params: query
    };
    return this.http.get<Array<Message>>(this.baseurl + '/messages',httpOptions).pipe(
        catchError(this.Messageservice.handleError<Array<Message>>('getMessages'))
        // map(x => ({
        //   createAt: new Date(x.createAt);
        // }))
    )
  }
}
