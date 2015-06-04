@author: Zhu Wang
@date: 4/21/2015
For a new conference/a new application:

-XML confirguration files:
--main_inferface.xml(/res/layout):
---Framelayout: the last one <textview>andriod:text=""(change HT2014 to related conference)
---<textview> contains "My HT 2014"(change andriod:text to related conference)

--sting.xml(/res/values)
---app_name

--AndriodManifest.xml(/res)
---package
---activity

--image and related xml(image:/res/drawable;xml:/res/layout)
---initial page backgroud: back1(xml: main.xml)
---index page backgroud:back2(xml:main_interface.xml)

-Source code:
--the classes with name contained parse: change the right url of different conference