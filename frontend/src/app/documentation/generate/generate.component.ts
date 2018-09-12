import {Component, EventEmitter, OnInit, Output} from '@angular/core';

@Component({
  selector: 'app-generate',
  templateUrl: './generate.component.html',
  styleUrls: ['./generate.component.scss']
})
export class GenerateComponent implements OnInit {

  constructor() { }

  @Output()
  httpParams: string;

  ngOnInit() {
  }

  generateDocumentationRequest(httpParams : string){
    this.httpParams = httpParams ;
  }

}
