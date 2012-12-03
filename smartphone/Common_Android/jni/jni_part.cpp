#include <jni.h>
#include <opencv2/core/core.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <opencv2/features2d/features2d.hpp>
#include <opencv2/highgui/highgui.hpp>
#include <vector>

using namespace std;
using namespace cv;

extern "C" {

//static VideoCapture* pCamera = NULL;
//static Mat lastFrame;
//static vector<KeyPoint> lastKeypoints;
//static Mat lastDescriptors;
//static bool first = true;
//
//JNIEXPORT jboolean JNICALL Java_teambots_smartphone_demo_videostreaming_NativeCamera_nOpenCamera(
//		JNIEnv* env, jlong native_pCamera) {
//
//	if (pCamera != NULL && pCamera->isOpened())
//		return true;
//
//	pCamera = new VideoCapture(1);
//	native_pCamera = (jlong)pCamera;
//
//	if (!pCamera->isOpened())
//		return false;
//
//	return true;
//}
//
//JNIEXPORT void JNICALL Java_teambots_smartphone_demo_videostreaming_NativeCamera_nCloseCamera(
//		JNIEnv* env) {
//	if(pCamera == NULL)
//		return;
//
//	delete(pCamera);
//	pCamera = NULL;
//}
//
//JNIEXPORT jdouble JNICALL Java_teambots_smartphone_demo_videostreaming_NativeCamera_nGetWidth(
//		JNIEnv* env) {
//	return pCamera->get(CV_CAP_PROP_FRAME_WIDTH);
//}
//
//JNIEXPORT jdouble JNICALL Java_teambots_smartphone_demo_videostreaming_NativeCamera_nGetHeight(
//		JNIEnv* env) {
//	return pCamera->get(CV_CAP_PROP_FRAME_HEIGHT);
//}
//
//JNIEXPORT jstring JNICALL Java_teambots_smartphone_demo_videostreaming_NativeCamera_nGetPreviewSize(
//		JNIEnv* env) {
//
//    union {double prop; const char* name;} u;
//    u.prop = pCamera->get(CV_CAP_PROP_SUPPORTED_PREVIEW_SIZES_STRING);
//
//   return env->NewStringUTF(u.name);
//}
//
//JNIEXPORT void JNICALL Java_teambots_smartphone_demo_videostreaming_NativeCamera_nSetPreviewSize(
//		JNIEnv* env, jdouble width, jdouble height) {
//
//	pCamera->set(CV_CAP_PROP_FRAME_WIDTH, width);
//	pCamera->set(CV_CAP_PROP_FRAME_HEIGHT, height);
//}
//
//JNIEXPORT jboolean JNICALL Java_teambots_smartphone_demo_videostreaming_NativeCamera_nReadFrame(
//		JNIEnv* env, jlong native_pFrame) {
//
//	if (pCamera == NULL || !pCamera->isOpened())
//			return false;
//
//	Mat* pFrame = (Mat*) native_pFrame;
//		return pCamera->read(*pFrame);
//}
//
//JNIEXPORT void JNICALL Java_teambots_smartphone_demo_videostreaming_NativeCamera_nStichFrames(
//		JNIEnv* env, jlong native_pResultImage) {
//
//	Mat newFrame;
//	vector<KeyPoint> newKeypoints;
//	Mat newDescriptors;
//
//	pCamera->read(newFrame);
//
//	cvtColor(newFrame, newFrame, CV_RGB2GRAY);
//
//    FastFeatureDetector detector(50);
//    detector.detect(newFrame, newKeypoints);
//    FREAK descriptor(2);
//    descriptor.compute(newFrame, newKeypoints, newDescriptors);
//
//	if(!lastFrame.empty()) {
//		Mat* pResult = (Mat*)native_pResultImage;
//
//		BFMatcher matcher(1);
//		vector< DMatch > matches;
//		matcher.match(lastDescriptors, newDescriptors, matches);
//		drawMatches(lastFrame, lastKeypoints, newFrame, newKeypoints, matches, *pResult);
//	}
//
//	lastFrame = newFrame;
//	lastKeypoints = newKeypoints;
//	lastDescriptors = newDescriptors;
//}

}
