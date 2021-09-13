# JavaImageMatching

University assignment for a group of three students. The goal is to find words in scanned documents. The program reads a few scanned pictures, modifies them, and searches for a given template picture. The output is the found position for every picture. The image processing tasks are parallelized.

The steps of the program are as follows:
1. Reading of a colored picture.
2. Creating a grayscale image of the picture.
3. Blurring of the grayscale image.
4. Template matching of the blurred image using least-squares.
5. Output of the found position
