import {Component, Input, OnInit} from '@angular/core';

@Component({
  selector: 'app-documentation-theme-book-long-text',
  templateUrl: './documentation-theme-book-long-text.component.html',
  styleUrls: ['./documentation-theme-book-long-text.component.scss']
})
export class DocumentationThemeBookLongTextComponent implements OnInit {

  @Input()
  longText: string;

  isJson: boolean;
  json : any;

  constructor() { }

  ngOnInit() {
    this.jsonText = this.longText;
  }

  set jsonText (text) {
    try{
      this.json = JSON.parse(text);
      this.isJson = true ;
    }
    catch(e) {
      this.isJson = false ;
    }
  }

}
