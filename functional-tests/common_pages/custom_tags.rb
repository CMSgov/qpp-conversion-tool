# require watir
#
# module Watir
#   module Container
#     def appallmodulestablerow(*args)
#       APPALLMODULESTABLEROW.new(self, extract_selector(args).merge(:tag_name => "app-all-modules-table-row"))
#     end
#
#     def appallmodulestablerows(*args)
#       APPALLMODULESTABLEROWCollection.new(self, extract_selector(args).merge(:tag_name => "app-all-modules-table-row"))
#     end
#   end
#
#   class APPALLMODULESTABLEROW < Element
#   end
#
#   class APPALLMODULESTABLEROWCollection < ElementCollection
#     def element_class
#       G
#     end
#   end
# end