import cv2
import numpy as np
import matplotlib.pyplot as plt
# import pytesseract

# 이미지 load
img = cv2.imread('number.jpg')
# img = cv2.imread('car1.jpg')
height, width, channel = img.shape
print(height, width, channel)

# grayscale 변환
gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)

# plt.imshow(gray, cmap='gray')
# plt.show()

"""Adaptive Thresholding"""
# 이미지 noise 줄이기
img_blurred = cv2.GaussianBlur(gray, ksize=(5, 5), sigmaX=0)
# Adaptive Thresholding == 0 or 255
img_thresh = cv2.adaptiveThreshold(
    img_blurred,
    maxValue=255.0,
    adaptiveMethod=cv2.ADAPTIVE_THRESH_GAUSSIAN_C,
    thresholdType=cv2.THRESH_BINARY_INV,
    blockSize=19,
    C=17
)
plt.figure(figsize=(12, 10))
plt.imshow(img_thresh, cmap='gray')
plt.show()
# cv2.imshow("aa", img_thresh)
# cv2.waitKey(0)
# cv2.destroyAllWindows()

img_thresh2 = cv2.GaussianBlur(img_thresh, ksize=(3, 3), sigmaX=0)
plt.figure(figsize=(12, 10))
plt.imshow(img_thresh2, cmap='gray')
plt.show()

# https://076923.github.io/posts/Python-opencv-36/