import { Component, ElementRef } from '@angular/core';
import { MessagingService } from './../messaging-service';
import { Message } from '../message';
import { Popupservice } from '../popupservice';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { RoutingService } from './../routing-service';
import { UserServiceService } from './../user-service.service';
import { Usertype } from '../user';
import { ActivatedRoute } from '@angular/router';
import { ViewChild } from '@angular/core';




@Component({
  selector: 'app-admin-chat',
  standalone: false,
  templateUrl: './admin-chat.html',
  styleUrl: './admin-chat.css'
})
export class AdminChat {
  limit: number = 50;
  loadMessages: boolean = false;
  messages: Message[] = [];
  messageform = new FormGroup({
    text: new FormControl("",[Validators.required,Validators.maxLength(256 * 2)])
  })

  // Grab the div that needs to auto scroll
  @ViewChild('scroll') private scroll!: ElementRef<HTMLDivElement>;


  constructor(private popupService: Popupservice,private activeroute: ActivatedRoute,private routingService: RoutingService,private messagingService: MessagingService, private userService: UserServiceService) {}

  username!: string;
  ngOnInit(): void {
    this.getMessages(this.limit);
    this.username = this.userService.getUserName();
    this.scrollToBottom(true);
  }



  onChatOpened() {
    this.getMessages(this.limit);
    this.username = this.userService.getUserName();
    this.scrollToBottom(true);
    setTimeout(() => {
      this.scrollToBottom(true);
    },0);
  }


  /**
   * Prompts messaging service to get all recent messages up to a limit
   * @param numToLoad the number of messages to get
   */
  getMessages(numToLoad: number, optional: {preservePosition?: boolean}={}): void {
    const el = this.scroll?.nativeElement;

    // Capture current state of scroll if want to preserve position
    const prevScrollHeight = optional.preservePosition && el ? el.scrollHeight :0;
    const prevScrollTop = optional.preservePosition && el ? el.scrollTop : 0;

    this.username = this.userService.getUserName();

    this.messagingService.getMessages(this.limit + numToLoad).subscribe({
      next: (data: Message[]) => {

        if(data.length > this.messages.length) {
          this.limit += numToLoad;
        }

        this.messages = data;
        setTimeout(() => {
          if(optional.preservePosition && el ) {
            const newScrollHeight = el.scrollHeight;
            const change = newScrollHeight - prevScrollHeight;

            el.scrollTop = prevScrollTop + change;
          } else {
            this.scrollToBottom(true); // scroll to bottom when new message added.\
          }

          this.canLoadMessages();
        }, 0);

      },
      error: () => {
        this.routingService.routeToLogin();
      }
    })
  }

  canLoadMessages(): void {
    this.messagingService.getMessages(this.messages.length + 1).subscribe({
      next: (data: Message[]) => {


        if (data.length > this.messages.length){
            this.loadMessages = true;
        } else {
            this.loadMessages = false;
        }
      },
      error: () => {
        this.loadMessages = false;
        this.routingService.routeToLogin();
      }
    })
  }

  /**
   * Prompts messaging service to send a message in the admin chat
   * @param event button event
   */
  sendMessage(event: Event): void {
    event.preventDefault();
    const message = this.messageform.get("text")?.value?.trim()
    if (message == undefined || message == null  || message == "" ) {return}

    this.messagingService.sendMessage(message).subscribe({
      next: () => {
        this.getMessages(this.limit);
      }
    });
    setTimeout(() => this.scrollToBottom(false), 0); // scroll to bottom when new messages added.
    this.messageform.reset();
  }

   /**
     * verifies if the current user is an admin
     * @returns if the user is an admin
     */
    isAdmin(): boolean {
      return this.userService.isUserType(Usertype.ADMIN);
    }

    // scroll to the bottom
    private scrollToBottom(instant: boolean): void {
      const el = this.scroll?.nativeElement;
      if(!el) return;

      if (instant) {
        const oldBehavior = el.style.scrollBehavior;
        el.style.scrollBehavior = 'auto';

        el.scrollTop = el.scrollHeight;

        el.style.scrollBehavior = oldBehavior || '';
      } else {

        el.scrollTop = el.scrollHeight;
      }
    }

    loadMore(numToLoad: number): void {
      this.getMessages(numToLoad, { preservePosition: true});
    }
}
