package models

import com.github.ghik.silencer.silent
import io.swagger.annotations.ApiModelProperty


case class Variable(name: String, value: String)

@silent("Interpolated")
@silent("missing interpolator")
case class Project(
                    @ApiModelProperty(value = "id of the project", example = "theGardener", required = true) id: String,
                    @ApiModelProperty(value = "name of the project", example = "theGardener", required = true) name: String,
                    @ApiModelProperty(value = "location of the project", example = "https://github.com/KelkooGroup/theGardener", required = true) repositoryUrl: String,
                    @ApiModelProperty(value = "source URL template", example = "https://github.com/KelkooGroup/theGardener/blob/${branch}/${path}", required = false) sourceUrlTemplate: Option[String],
                    @ApiModelProperty(value = "stableBranch of the project", example = "master", required = true) stableBranch: String,
                    @ApiModelProperty(value = "branches that will be displayed", example = "qa|master|feature.*|bugfix.*") displayedBranches: Option[String] = None,
                    @ApiModelProperty(value = "path that lead to the feature files", example = "test/features") featuresRootPath: Option[String],
                    @ApiModelProperty(value = "path that lead to the documentation files", example = "documentation") documentationRootPath: Option[String] = None,
                    @ApiModelProperty(value = "variables defined for this project", example = "[{\"name\":\"${swagger.url}\",\"value\":\"http://dc1-pmbo-corp-srv-pp.corp.dc1.kelkoo.net:9001/docs\"}]") variables: Option[Seq[Variable]] = None,
                    @ApiModelProperty(value = "Hierarchy matching the project") hierarchy: Option[Seq[HierarchyNode]] = None,
                    @ApiModelProperty(value = "branches of the project") branches: Option[Seq[Branch]] = None)

case class ProjectBranch(project:Project,branch:Branch)