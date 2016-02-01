# LevelAnimationDrawable

an android animation demo base on drawable.

It is a simple framework based on the core of the Android Property Animation.

if you want to know more about how to make your own animation framework, you can see [here.](http://hoyoshaw.github.io/2016/02/01/%E6%89%93%E9%80%A0%E8%87%AA%E5%B7%B1%E7%9A%84Android%E5%8A%A8%E7%94%BB%E6%A1%86%E6%9E%B6/)

## How to use
- define a ImageView in your layout XML file.
```xml
<ImageView
    android:id="@+id/iv_wave"
    android:layout_width="match_parent"
    android:layout_height="match_parent"/>
```
- extend LevelAnimationDrawable and implements draw method.
```java
 @Override
    public void draw(Canvas canvas) {

        float ratio = getLevel() / 10000f;
        if (ratio < 0.01f) {
            ratio = 0.01f;
        }
        ...
    }
```
- start animation in activity.
```java
if (waveDrawable == null){
            waveDrawable = new XXXDrawable(this);
            waveDrawable.setBounds(width, height);
            waveDrawable.setDuration(1000);
            waveDrawable.animateTo(60);
            
            waveDrawable.setAnimationCallback(new LevelAnimationDrawable.AnimationCallback() {

                @Override
                public void onLevelChanged(int level) {

                }

                @Override
                public void onAnimationEnd() {
                  
                }
            });
            vWaveView.setImageDrawable(waveDrawable);
        }
```



