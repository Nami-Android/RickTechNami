package com.app.ricktech.general_ui_method;

import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.databinding.BindingAdapter;

import com.app.ricktech.R;
import com.app.ricktech.tags.Tags;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class GeneralMethod {

    @BindingAdapter("error")
    public static void errorValidation(View view, String error) {
        if (view instanceof EditText) {
            EditText ed = (EditText) view;
            ed.setError(error);
        } else if (view instanceof TextView) {
            TextView tv = (TextView) view;
            tv.setError(error);


        }
    }








    @BindingAdapter("image")
    public static void image(View view, String endPoint) {


        String url =Uri.parse(Tags.IMAGE_URL+endPoint).toString();

        if (view instanceof CircleImageView) {
            CircleImageView imageView = (CircleImageView) view;
            if (endPoint != null) {

                Picasso.get().load(Uri.parse(Tags.IMAGE_URL + endPoint)).into(imageView);
            }
        } else if (view instanceof RoundedImageView) {
            RoundedImageView imageView = (RoundedImageView) view;

            if (endPoint != null) {

                Picasso.get().load(Uri.parse(Tags.IMAGE_URL + endPoint)).into(imageView);
            }
        } else if (view instanceof ImageView) {
            ImageView imageView = (ImageView) view;

            if (endPoint != null) {

                Picasso.get().load(Uri.parse(Tags.IMAGE_URL + endPoint)).into(imageView);
            }
        }

    }

    @BindingAdapter("user_image")
    public static void user_image(View view, String endPoint) {
        if (view instanceof CircleImageView) {
            CircleImageView imageView = (CircleImageView) view;

            if (endPoint != null) {
                Picasso.get().load(Uri.parse(Tags.IMAGE_URL + endPoint)).placeholder(R.drawable.ic_avatar).into(imageView);
            }
        } else if (view instanceof RoundedImageView) {
            RoundedImageView imageView = (RoundedImageView) view;
            if (endPoint != null) {
                Picasso.get().load(Uri.parse(Tags.IMAGE_URL + endPoint)).fit().placeholder(R.drawable.ic_avatar).into(imageView);
            }
        } else if (view instanceof ImageView) {
            ImageView imageView = (ImageView) view;
            if (endPoint != null) {
                Picasso.get().load(Uri.parse(Tags.IMAGE_URL + endPoint)).placeholder(R.drawable.ic_avatar).fit().into(imageView);
            }
        }

    }



    @BindingAdapter("compare")
    public static void compare(TextView view, double point) {
        if (point>=80){
            view.setBackgroundResource(R.drawable.rounded_perfect);
        }else if (point>=50&&point<80){
            view.setBackgroundResource(R.drawable.rounded_good);

        }else {
            view.setBackgroundResource(R.drawable.rounded_bad);

        }
        view.setText(point+"%");

    }

    @BindingAdapter("date")
    public static void date(TextView view, String create_at) {
       if (create_at!=null){
           String[] arr = create_at.split("T");
           if (arr.length>0){
               view.setText(arr[0]);
           }
       }

    }

    @BindingAdapter("status")
    public static void status(TextView view, String status) {
       switch (status){
           case "new":
               view.setText(R.string.new1);
               break;
           case "pending":
               view.setText(R.string.pending);
               break;
           case "is_delivering":
               view.setText(R.string.delivering);

               break;
           case "user_canceled":
               view.setText(R.string.user_canceled);

               break;
           case "admin_canceled":
               view.setText(R.string.admin_cancel);

               break;
           case "backed_request":
               view.setText(R.string.back_req);

               break;

           default:
               view.setText(R.string.backed);

               break;
       }

    }

}










