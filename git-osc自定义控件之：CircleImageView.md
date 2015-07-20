# git-osc自定义控件之：CircleImageView


## 一、CircleImageView的使用

在项目中可以发现，用户的头像都是圆形的，感觉很好奇，昨天终于发现了，原来是自定了一个ImageView，先学习下如何使用，使用步骤如下：

1. 创建属性文件：`attrs.xml`，创建路径为—— `res/values/attrs.xml` ，格式如下：
	
	```
	<?xml version="1.0" encoding="utf-8"?>
		<resources>
    		<declare-styleable name="CircleImageView">
        			<attr name="border_width" format="dimension" />
        			<attr name="border_color" format="color" />
    		</declare-styleable>
		</resources>
	```
	
2. 	将`CircleImageView.java`拷贝到项目工程中，比如在路径为：`com.nc.view` 中
3. 	在布局文件中引入，自定义控件，代码如下：

```
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    tools:context=".MainActivity">

    <com.nc.view.CircleImageView
        android:id="@+id/circle_img"
        android:layout_weight="1"
        ....
        />
</LinearLayout>

```

做完上述三步就可以在自己的项目中使用这个自定义控件：CircleImageView.java 了。

## 二、CircleImageView 解析

CircleImageView中使用到的知识点：

`TypedArray 、declare-styleable、ColorFilter、ScaleType、Bitmap.Config、Canvas、Matrix、BitmapShaderPaint`

### TypedArray
***

> Container for an array of values that were retrieved with obtainStyledAttributes(AttributeSet, int[], int, int) or obtainAttributes(AttributeSet, int[]). Be sure to call recycle() when done with them. The indices used to retrieve values from this structure correspond to the positions of the attributes given to obtainStyledAttributes.

> 包含了一个数组值，这个数组值会被 `obtainStyledAttributes(AttributeSet, int[], int, int)` or `obtainAttributes(AttributeSet, int[])`方法拿到。但是确保在拿到数组中的值后要调用 recycle()方法。我们可以通过索引值，在`obtainStyledAttributes`的属性中检索相对应的值。

那么该如何使用呢，代码如下：

```java
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.CircleImageView, defStyle, 0);

		mBorderWidth = a.getDimensionPixelSize(
				R.styleable.CircleImageView_border_width, DEFAULT_BORDER_WIDTH);
		mBorderColor = a.getColor(R.styleable.CircleImageView_border_color,
				DEFAULT_BORDER_COLOR);

		a.recycle();
```
上面就是一个具体的应用：

1. 通过`obtainStyledAttributes` 获取 `TypedArray` , 其属性值存放在 `res/attrs.xml`中的`declare-styleable`中，关于`declare-styleable`在下面再具体描述；
2. 通过`TypedArray`提供的一系列方法`getXXXX`获取对应的属性；
3. 拿完属性之后，要调用 `recycle()`。

###declare-styleable
***
如上代码所示，属性值都是存在了`declare-styleable`中，而`declare-styleable`则是在  `res/attrs.xml` 的 `<resource>` 节点下，代码详情如下：

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <declare-styleable name="CircleImageView">
        <attr name="border_width" format="dimension" />
        <attr name="border_color" format="color" />
    </declare-styleable>
</resources>
```
可以看到:

1. 通过 `name="CircleImageView"` 属性来给`declare-styleable`命令，该名字将会在`obtainStyledAttributes(attrs,R.styleable.CircleImageView, defStyle, 0)` 中使用。

2. 通过 `<attr .../>` 来给`CircleImageView` 指定属性：
	> 1. `<attr name="xxx".../>` 其中的 `name="xxx"` 来指定该属性的名字；
	> 2. `<attr ... format="xxx"/>` 其中的 `format="xxx"` 来指定该属性的类型；

其中的`format`共有如下类型,可以参见文章：（format详解）[http://www.cnblogs.com/622698abc/p/3348692.html]

* reference ：参考某一资源ID
* string    ：字符串
* color     ：颜色值
* dimension ：尺寸值
* boolean   ：布尔值
* integer   ：整型值
* float	   ：浮点值
* fraction  ：百分数
* enum	   ：枚举值
* flag	   ：位或运算
### ColorFilter
色彩过滤器，用法：

```
 //初始化Paint，并且设置消除锯齿。  
Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG); 
ColorMatrixColorFilter colorFilter = new ColorMatrixColorFilter(new float[]{  
                1, 0, 0, 0, 0,  
                0, 1, 0, 0, 0,  
                0, 0, 1, 0, 0,  
                0, 0, 0, 1, 0  
        });  
paint.SetColorFilter(colorFilter);
```
两篇不错的关于ColorFilter的博客：

> 1. [ Android图像处理——Paint之ColorFilter](http://blog.csdn.net/allen315410/article/details/45059989)
> 2. [用ColorFilter为安卓按钮增加效果](http://www.jianshu.com/p/9cae2250d0ed)

### ScaleType
图片的缩放，共有8个属性：
> matrix、fitXY、fitStart、fitCenter、fitEnd、center、centerCrop、centerInside 

具体用法参见博客： 

 [在android中设置图片的scaleType属性](http://www.chenyunchao.com/?p=189)
### Bitmap.Config

* Bitmap.Config ALPHA_8   
* Bitmap.Config ARGB_4444   
* Bitmap.Config ARGB_8888   
* Bitmap.Config RGB_565  

　　A　　R　　G　　B
透明度　红色　绿色　蓝色

Bitmap.Config ARGB_4444 16位每个像素 占四位   
Bitmap.Config ARGB_8888 32位每个像素 占八位  
Bitmap.Config RGB_565   16位R占5位 G占6位 B占5位 没有透明度（A）

一般情况下我们都是用argb888 但是无可厚非 它也相对的很占内存
因为一个像素32位 8位一个字节 如果是800*480的图片的话自己算 估计有1M多了

### Canvas

大部分2D使用的api都在android.graphics和android.graphics.drawable包。他们提供了图形处理相关的：Canvas、ColorFilter、Point(点)和RetcF(矩形)等，还有一些动画相关的：AnimationDrawable、 BitmapDrawable和TransitionDrawable等。以图形处理来说，我们最常用到的就是在一个View上画一些图片、形状或者自定义的文本内容，这里我们都是使用Canvas来实现的。你可以获取View中的Canvas对象，绘制一些自定义形状，然后调用View. invalidate方法让View重新刷新，然后绘制一个新的形状，这样达到2D动画效果。下面我们就主要来了解下Canvas的使用方法。

Canvas对象的获取方式有两种：

> * 一种我们通过重写View.onDraw方法，View中的Canvas对象会被当做参数传递过来，我们操作这个Canvas，效果会直接反应在View中。
> * 另一种就是当你想创建一个Canvas对象时使用的方法：

	Bitmap b = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);   
	Canvas c = new Canvas(b);
	
通过Canvas绘制的图形有：弧线(arcs)、填充颜色(argb和color)、 Bitmap、圆(circle和oval)、点(point)、线(line)、矩形(Rect)、图片(Picture)、圆角矩形 (RoundRect)、文本(text)、顶点(Vertices)、路径(path)

Canvas提供的位置转换方法：rorate、scale、translate、skew(扭曲)

### Matrix 

对于一个图片变换的处理，需要Matrix类的支持，它位于"android.graphics.Matrix"包下，是Android提供的一个矩阵工具类，它本身不能对图像或View进行变换，但它可与其他API结合来控制图形、View的变换，如Canvas。

Matrix提供了一些方法来控制图片变换：

* setTranslate(float dx,float dy)：控制Matrix进行位移。
* setSkew(float kx,float ky)：控制Matrix进行倾斜，kx、ky为X、Y方向上的比例。
* setSkew(float kx,float ky,float px,float py)：控制Matrix以px、py为轴心进行倾斜，kx、ky为X、Y方向上的倾斜比例。
* setRotate(float degrees)：控制Matrix进行depress角度的旋转，轴心为（0,0）。
* setRotate(float degrees,float px,float py)：控制Matrix进行depress角度的旋转，轴心为(px,py)。
* setScale(float sx,float sy)：设置Matrix进行缩放，sx、sy为X、Y方向上的缩放比例。
* setScale(float sx,float sy,float px,float py)：设置Matrix以(px,py)为轴心进行缩放，sx、sy为X、Y方向上的缩放比例。

　　之前有提过，图片在内存中存放的就是一个一个的像素点，而对于图片的变换主要是处理图片的每个像素点，对每个像素点进行相应的变换，即可完成对图像的变换。
　　
### Paint

Paint代表Canvas上的画刷，主要用于绘制风格，包括画刷颜色、画刷笔触粗细、填充风格等。
大体上可以分为两类，一类与图形绘制相关，一类与文本绘制相关。

### BitmapShader
位图渲染器，使用BitmapShader方法可以实现圆形、圆角和椭圆的绘制

BitmapShader是Shader的子类，可以通过Paint.setShader（Shader shader）进行设置、

我们这里只关注BitmapShader，构造方法：

mBitmapShader = new BitmapShader(bitmap, TileMode.CLAMP, TileMode.CLAMP);

参数1：bitmap

参数2，参数3：TileMode；

TileMode的取值有三种：

* CLAMP 拉伸

* REPEAT 重复

* MIRROR 镜像

以下是代码部分：

```
package com.nfschina.circleimageview.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import com.nfschina.circleimageview.R;

//继承自 ImageView
public class CircleImageView extends ImageView {

	private static final ScaleType SCALE_TYPE = ScaleType.CENTER_CROP;

	private static final Bitmap.Config BITMAP_CONFIG = Bitmap.Config.ARGB_8888;

	// colordrawable_dimension
	private static final int COLORDRAWABLE_DIMENSION = 1;

	// default_border_width
	private static final int DEFAULT_BORDER_WIDTH = 0;
	// default_border_color
	private static final int DEFAULT_BORDER_COLOR = Color.BLACK;

	//
	private final RectF mDrawableRect = new RectF();
	private final RectF mBorderRect = new RectF();

	private final Matrix mShaderMatrix = new Matrix();
	private final Paint mBitmapPaint = new Paint();
	private final Paint mBorderPaint = new Paint();

	private int mBorderColor = DEFAULT_BORDER_COLOR;
	private int mBorderWidth = DEFAULT_BORDER_WIDTH;

	private Bitmap mBitmap;
	private BitmapShader mBitmapShader;
	private int mBitmapWidth;
	private int mBitmapHeight;

	private float mDrawableRadius;
	private float mBorderRadius;

	private boolean mReady;
	private boolean mSetupPending;

	public CircleImageView(Context context) {
		super(context);
	}

	public CircleImageView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public CircleImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		super.setScaleType(SCALE_TYPE);

		Log.i("circle" , "CircleImageView");

		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.CircleImageView, defStyle, 0);

		mBorderWidth = a.getDimensionPixelSize(
				R.styleable.CircleImageView_border_width, DEFAULT_BORDER_WIDTH);
		mBorderColor = a.getColor(R.styleable.CircleImageView_border_color,
				DEFAULT_BORDER_COLOR);

		a.recycle();

		mReady = true;

		if (mSetupPending) {
			setup();
			mSetupPending = false;
		}
	}

	@Override
	public ScaleType getScaleType() {

		Log.i("circle" , "getScaleType");
		return SCALE_TYPE;
	}

	@Override
	public void setScaleType(ScaleType scaleType) {

		Log.i("circle" , "setScaleType");
		if (scaleType != SCALE_TYPE) {
			throw new IllegalArgumentException(String.format(
					"ScaleType %s not supported.", scaleType));
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {

		Log.i("circle" , "onDraw");
		if (getDrawable() == null) {
			return;
		}

		canvas.drawCircle(getWidth() / 2, getHeight() / 2, mDrawableRadius,
				mBitmapPaint);
		if (mBorderWidth != 0) {
			canvas.drawCircle(getWidth() / 2, getHeight() / 2, mBorderRadius,
					mBorderPaint);
		}
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);

		Log.i("circle", "onSizeChanged");
		setup();
	}

	public int getBorderColor() {

		Log.i("circle" , "getBorderColor");
		return mBorderColor;
	}

	public void setBorderColor(int borderColor) {


		Log.i("circle" , "setBorderColor");
		if (borderColor == mBorderColor) {
			return;
		}

		mBorderColor = borderColor;
		mBorderPaint.setColor(mBorderColor);
		invalidate();
	}

	public int getBorderWidth() {

		Log.i("circle" , "getBorderWidth");

		return mBorderWidth;
	}

	public void setBorderWidth(int borderWidth) {

		Log.i("circle" , "setBorderWidth");
		if (borderWidth == mBorderWidth) {
			return;
		}

		mBorderWidth = borderWidth;
		setup();
	}

	/*
	 * 给ImageView设置图片的方法就是setImageXXX，不管你是用JAVA代码设置，还是在XML里面用src，
	 * 它最终还是调用JAVA的setImageXXX方法,在这些个方法的最后一定有一个invalidate的方法，让其调用onDraw。
	 * 
	 * 它们在设置完图片之后必定会调用setup方法，其实这个方法就是一个初始化操作而已， 但是在setup的结束地方也调用了invalidate
	 */
	@Override
	public void setImageBitmap(Bitmap bm) {
		super.setImageBitmap(bm);

		Log.i("circle", "setImageBitmap");
		mBitmap = bm;
		setup();
	}

	@Override
	public void setImageDrawable(Drawable drawable) {

		super.setImageDrawable(drawable);

		Log.i("circle", "setImageDrawable");
		mBitmap = getBitmapFromDrawable(drawable);
		setup();
	}

	@Override
	public void setImageResource(int resId) {
		super.setImageResource(resId);

		Log.i("circle", "setImageResource");
		mBitmap = getBitmapFromDrawable(getDrawable());
		setup();
	}

	private Bitmap getBitmapFromDrawable(Drawable drawable) {

		Log.i("circle" , "getBitmapFromDrawable");
		if (drawable == null) {
			return null;
		}

		if (drawable instanceof BitmapDrawable) {
			return ((BitmapDrawable) drawable).getBitmap();
		}

		try {
			Bitmap bitmap;

			if (drawable instanceof ColorDrawable) {
				bitmap = Bitmap.createBitmap(COLORDRAWABLE_DIMENSION,
						COLORDRAWABLE_DIMENSION, BITMAP_CONFIG);
			} else {
				bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
						drawable.getIntrinsicHeight(), BITMAP_CONFIG);
			}

			Canvas canvas = new Canvas(bitmap);
			drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
			drawable.draw(canvas);
			return bitmap;
		} catch (OutOfMemoryError e) {
			return null;
		}
	}

	/*
	 * 它巧妙的用这个mBitmap为空，如果这个View还没有被设置图片则不会执行下面的方法
	 * 
	 * 这个方法应当是在onMeasure之后调用的，我们知道图片设置成功之后，一定是在onMeasure之后，
	 * 
	 *    所以它并没有逃脱自定义控件的的逻辑
	 *    
	 *    1、重写onMeasure计算或者设置一些高度、宽度等数据，
	 *    2、重写onDraw 
	 *    3、重写onTouch做一些事件处理或者计算，
	 *    
	 *    它只是通过一个mBitmap的防空来判断它是在绘制成功之后的做的事情。
	 */
	private void setup() {

		Log.i("circle" , "setup");
		if (!mReady) {
			mSetupPending = true;
			return;
		}

		//这个mBitmap比较妙，只有当有图片被设置以后才会开始执行下面的方法。
		if (mBitmap == null) {
			return;
		}

		//使用一张位图作为纹理对某一区域进行填充，例如我们drawCircle，
		//它就会对这个circle里面进行填充,它的两个参数就是对X，Y轴进行填充的一些规则。
		mBitmapShader = new BitmapShader(mBitmap, Shader.TileMode.CLAMP,
				Shader.TileMode.CLAMP);

		mBitmapPaint.setAntiAlias(true);
		mBitmapPaint.setShader(mBitmapShader);

		mBorderPaint.setStyle(Paint.Style.STROKE);
		mBorderPaint.setAntiAlias(true);
		mBorderPaint.setColor(mBorderColor);
		mBorderPaint.setStrokeWidth(mBorderWidth);

		mBitmapHeight = mBitmap.getHeight();
		mBitmapWidth = mBitmap.getWidth();

		// 整个图像的显示区域：即全部的View大小区域。
		mBorderRect.set(0, 0, getWidth(), getHeight());
		mBorderRadius = Math.min((mBorderRect.height() - mBorderWidth) / 2,
				(mBorderRect.width() - mBorderWidth) / 2);

		// 图片显示的区域：即View的大小区域减去边界的大小。
		mDrawableRect.set(mBorderWidth, mBorderWidth, mBorderRect.width()
				- mBorderWidth, mBorderRect.height() - mBorderWidth);
		// 图片的半径大小取图片小边。
		mDrawableRadius = Math.min(mDrawableRect.height() / 2,
				mDrawableRect.width() / 2);

		updateShaderMatrix();
		invalidate();
	}

	private void updateShaderMatrix() {

		Log.i("circle" , "updateShaderMatrix");
		float scale;
		float dx = 0;
		float dy = 0;

		mShaderMatrix.set(null);

		if (mBitmapWidth * mDrawableRect.height() > mDrawableRect.width()
				* mBitmapHeight) {
			scale = mDrawableRect.height() / (float) mBitmapHeight;
			dx = (mDrawableRect.width() - mBitmapWidth * scale) * 0.5f;
		} else {
			scale = mDrawableRect.width() / (float) mBitmapWidth;
			dy = (mDrawableRect.height() - mBitmapHeight * scale) * 0.5f;
		}

		mShaderMatrix.setScale(scale, scale);
		mShaderMatrix.postTranslate((int) (dx + 0.5f) + mBorderWidth,
				(int) (dy + 0.5f) + mBorderWidth);

		mBitmapShader.setLocalMatrix(mShaderMatrix);
	}

}

```

这里涉及到了自定义控件的知识，接下来几天里，我将重点学习一下自定义控件，在这个项目里还有一个：
`PagerSlidingTabStrip`。接下来学习一下这个，最后，重点总结一下——

自定义控件的方法







	

