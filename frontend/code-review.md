- The hierarchy of the _components folder is a bit messy. I would prefer : 
   > _components
   >> menu
   >>
   >>> menu
   >>>
   >>> menu-item 
   >>
   >> header
   >>
   >> footer
   >>
   >> page
   >>
   >> panel

- The best practice for models is to use Interfaces, not classes. Interfaces are used by Typescript compiler for type checking but don't require any code to be generated in the JS bundle so they are lighter than classes, and usually enough. Classes can be used if a specific action is required on constructor.

- ExpandableNode getChilden: typo, should be getChild**r**en
- I think instead of using a static method in class DocumentationNode, it would be better to define a method in the service to build the object instances.

