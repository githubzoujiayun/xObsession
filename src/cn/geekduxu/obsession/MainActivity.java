package cn.geekduxu.obsession;

import java.io.File;
import java.io.FileOutputStream;
import java.util.UUID;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private static final int CUT_IMAGE_CODE = 0x3673;

	private ImageView[] nums;

	private ImageView targetImage;
	private Bitmap srcBitmap;
	private Bitmap drawingBitmap;
	private Canvas canvas;
	private Paint paint;
	private Matrix matrix;
	private int width;
	private int height;

	private File fileRoot;
	private File tempFile;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		init();
	}

	private void init() {

		targetImage = (ImageView) findViewById(R.id.image);
		srcBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.test);
		targetImage.setImageBitmap(srcBitmap);
		this.width = srcBitmap.getWidth();
		this.height = srcBitmap.getHeight();

		targetImage.setOnClickListener(clickListener);

		nums = new ImageView[10];
		nums[0] = (ImageView) findViewById(R.id.n1);
		nums[1] = (ImageView) findViewById(R.id.n2);
		nums[2] = (ImageView) findViewById(R.id.n3);
		nums[3] = (ImageView) findViewById(R.id.n4);
		nums[4] = (ImageView) findViewById(R.id.n5);
		nums[5] = (ImageView) findViewById(R.id.n6);
		nums[6] = (ImageView) findViewById(R.id.n7);
		nums[7] = (ImageView) findViewById(R.id.n8);
		nums[8] = (ImageView) findViewById(R.id.n9);
		nums[9] = (ImageView) findViewById(R.id.n99);
		for (int i = 0; i < nums.length; i++) {
			nums[i].setOnClickListener(clickListener);
		}

		fileRoot = new File(Environment.getExternalStorageDirectory(), "obsession");
	}

	/**
	 * 点击数字图片时的操作
	 */
	private OnClickListener clickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.n1:
				drawNumber(0);
				break;
			case R.id.n2:
				drawNumber(1);
				break;
			case R.id.n3:
				drawNumber(2);
				break;
			case R.id.n4:
				drawNumber(3);
				break;
			case R.id.n5:
				drawNumber(4);
				break;
			case R.id.n6:
				drawNumber(5);
				break;
			case R.id.n7:
				drawNumber(6);
				break;
			case R.id.n8:
				drawNumber(7);
				break;
			case R.id.n9:
				drawNumber(8);
				break;
			case R.id.n99:
				drawNumber(9);
				break;
			case R.id.image:
				Intent intent = new Intent("android.intent.action.PICK");
				intent.setDataAndType(MediaStore.Images.Media.INTERNAL_CONTENT_URI, "image/*");
				tempFile = new File(fileRoot, System.currentTimeMillis() + ".png");
				intent.putExtra("output", Uri.fromFile(tempFile));
				intent.putExtra("crop", "true");
				intent.putExtra("aspectX", 1);
				intent.putExtra("aspectY", 1);
				intent.putExtra("outputX", 256);
				intent.putExtra("outputY", 256);
				startActivityForResult(intent, CUT_IMAGE_CODE);
				break;
			}
		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		if (resultCode == RESULT_OK && requestCode == CUT_IMAGE_CODE) {
			srcBitmap = BitmapFactory.decodeFile(tempFile.getAbsolutePath());
			targetImage.setImageBitmap(srcBitmap);
			width = srcBitmap.getWidth();
			height = srcBitmap.getHeight();
			tempFile.delete();
			drawingBitmap = null;
		}
	}

	private void drawNumber(int n) {
		float multiple = (width > height ? width : height) / (4 * 128.0f);
		matrix = new Matrix();
		matrix.postScale(multiple, multiple);
		Bitmap resizeBmp = Bitmap.createBitmap(
				((BitmapDrawable) nums[n].getDrawable()).getBitmap(), 0, 0,
				128, 128, matrix, true);
		drawingBitmap = Bitmap.createBitmap(srcBitmap.getWidth(),
				srcBitmap.getHeight(), srcBitmap.getConfig());
		canvas = new Canvas(drawingBitmap);
		paint = new Paint();
		canvas.drawBitmap(srcBitmap, 0, 0, paint);
		canvas.drawBitmap(resizeBmp, width * 0.75f, 0, paint);
		targetImage.setImageBitmap(drawingBitmap);
	}

	public void save(View view) {
		if (save() != null) {
			Toast.makeText(MainActivity.this, "已经保存到obsession文件夹内。",
					Toast.LENGTH_SHORT).show();
			return;
		}
		Toast.makeText(MainActivity.this, "抱歉，保存失败。", Toast.LENGTH_SHORT)
				.show();
	}

	private String save() {
		Bitmap doneBitmap = ((BitmapDrawable) targetImage.getDrawable())
				.getBitmap();
		String uuid = UUID.randomUUID().toString();
		uuid = uuid.substring(1 + uuid.lastIndexOf('-')) + ".png";
		File saveFile = new File(fileRoot, uuid);

		try {
			saveFile.createNewFile();
			FileOutputStream fOut = new FileOutputStream(saveFile);
			doneBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
			fOut.flush();
			fOut.close();
		} catch (Exception e) {
			return null;
		}
		return saveFile.getAbsolutePath();
	}

	public void share(View view) {

		if (drawingBitmap == null) {
			Toast.makeText(MainActivity.this, "还没有选择小角标哦 ^-^ ",
					Toast.LENGTH_SHORT).show();
			return;
		}

		Intent intent = new Intent(Intent.ACTION_SEND);
		File shareFile = new File(save());
		if (shareFile != null && shareFile.exists() && shareFile.isFile()) {
			intent.setType("image/png");
			Uri u = Uri.fromFile(shareFile);
			intent.putExtra(Intent.EXTRA_STREAM, u);
			intent.putExtra(Intent.EXTRA_SUBJECT, "逼死强迫症的头像。");
			intent.putExtra(Intent.EXTRA_TEXT,
					"分享一个逼死强迫症的头像，来自geekduxu[http://blog.csdn.net/duxu0711]制作的小程序。");
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
		}
	}

}
