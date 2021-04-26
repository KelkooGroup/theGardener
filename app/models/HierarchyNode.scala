package models

import io.swagger.annotations.ApiModelProperty

case class HierarchyNode(
                          @ApiModelProperty(value = "id of the node, define the hierarchy by it's structure, should match the pattern (\\.[0-9]+)*\\.).", example = ".01.02.01.", required = true) id: String,
                          @ApiModelProperty(value = "name of the node used in the url", example = "tools", required = true) slugName: String,
                          @ApiModelProperty(value = "label of the node used in the application", example = "tools", required = true) name: String,
                          @ApiModelProperty(value = "child element type (plural)",example = "project") childrenLabel: String,
                          @ApiModelProperty(value = "child element type (singular)",example = "project") childLabel: String,
                          @ApiModelProperty(value = "directory of pages displayed at this level, use the backend representation",example = "publisherData>master>/Public/") directoryPath: Option[String]= None,
                          @ApiModelProperty(value = "shortcut to another node of the hierarchy, use the id",example = ".02.01.") shortcut: Option[String]= None)

object HierarchyNode {
  val idSeparator = "."
}
