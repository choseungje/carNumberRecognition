@echo on
cd routes
cd darknet
darknet.exe detector test data/obj.data yolov3-custom-test.cfg backup/yolov3-custom_last.weights ccar.jpg