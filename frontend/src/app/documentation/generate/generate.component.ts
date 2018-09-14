import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {ActivatedRoute, Params, Router} from "@angular/router";
import {HttpParams} from "@angular/common/http";

@Component({
  selector: 'app-generate',
  templateUrl: './generate.component.html',
  styleUrls: ['./generate.component.scss']
})
export class GenerateComponent implements OnInit {

  @Output()
  isCriterias = false ;

  @Output()
  isOutput = false ;

  constructor(private route: ActivatedRoute, private router: Router){

  }

  ngOnInit() {
    this.route.params
      .subscribe((params: Params) => {
          var type = params['type'];
          this.isCriterias = type == "criterias" ;
          this.isOutput    = type == "output" ;
    });
  }

  generateDocumentationRequest(httpParams : HttpParams){
    var httpParamsAsString = httpParams.toString();
    this.router.navigateByUrl(`app/documentation/generate/output?${httpParamsAsString}`);
  }

}
