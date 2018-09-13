import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {ActivatedRoute, Params, Router} from "@angular/router";

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

  @Output()
  projects: string;

  constructor(private route: ActivatedRoute, private router: Router){

  }

  ngOnInit() {
    this.route.params
      .subscribe((params: Params) => {
          var type = params['type'];
          this.isCriterias = type == "criterias" ;
          this.isOutput    = type == "output" ;
    });

    this.route.queryParams.subscribe(params => {
      this.projects = params.projects;
    });

  }

  generateDocumentationRequest(httpParams : string){
    this.router.navigateByUrl(`app/documentation/generate/output?${httpParams}`);
  }

}
