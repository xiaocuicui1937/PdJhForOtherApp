 private static CheckUpdateInfo mCheckUpdateInfo;

    /**
     * 检查版本更新，如果有新版本就直接更新，没有新版本的不显示更新dialog
     * 更新有强制更新和非强制更新
     *
     * @param isShowTip 是否显示toast，提示用户最新版本
     */
//    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void checkUpdate(final Context ctx, final boolean isShowTip) {
        final Context context = App.getCtx();

        Q.checkUpdate(Q.GET, AppUtils.getAppVersionCode(), Contacts.REFRESHURL, null, new CheckUpdateCallback() {
            @Override
            public void onCheckUpdateSuccess(String result, boolean hasUpdate) {
                UpdateBean bean = new Gson().fromJson(result, UpdateBean.class);
                showUpdateText(bean);//展示更新的信息如更新信息的描述、发布时间、apk大小等
                if (hasUpdate) {//如果有更新
                    if (mCheckUpdateInfo.getIsForceUpdate() == 0) {//新版本号等于0，非强制更新
                        UpdateDialogClick(ctx);
                    } else {// 1强制更新
                        forceUpdateDialogClick(ctx);
                    }
                } else {//如果没有更新，提示用户当前是最新版本
                    if (isShowTip) {
                        ToastUtils.showShort(context.getString(R.string.tip_new_version));
                    }
                }
            }


            @Override
            public void onCheckUpdateFailure(String failureMessage, int errorCode) {
                LogUtils.i(failureMessage+errorCode);
                ToastUtils.showShort(context.getString(R.string.tip_connect_error));
            }
        });


    }


    /**
     * 展示版本更新的详细信息 新版本内容详述、发布事件、apk大小、版本号
     */
    private static void showUpdateText(UpdateBean bean) {
        mCheckUpdateInfo = new CheckUpdateInfo();
        mCheckUpdateInfo.setAppName(bean.updateTitle)
                .setIsForceUpdate(bean.updateForce)//设置是否强制更新，1表示非强制更新
                .setNewAppReleaseTime(bean.releaseTime)//软件发布时间
                .setNewAppSize(bean.apkSize)//单位为M
                .setNewAppUrl(Contacts.BASEPATHDOWNLOAD + bean.versionUrl)
                .setNewAppVersionCode(bean.newAppVersionCode)//新app的VersionCode
                .setNewAppVersionName(bean.newAppVersionName)
                .setNewAppUpdateDesc(bean.versionDescription);
    }

    /**
     * 强制更新
     */
    private static void forceUpdateDialogClick(Context ctx) {
        new ForceUpdateDialog(ctx).setAppSize(mCheckUpdateInfo.getNewAppSize())
                .setDownloadUrl(mCheckUpdateInfo.getNewAppUrl())
                .setTitle(mCheckUpdateInfo.getAppName() + "有更新啦")
                .setReleaseTime(mCheckUpdateInfo.getNewAppReleaseTime())
                .setVersionName(mCheckUpdateInfo.getNewAppVersionName())
                .setUpdateDesc(mCheckUpdateInfo.getNewAppUpdateDesc())
                .setFileName(App.getCtx().getString(R.string.app_name) + ".apk")
                .setFilePath(Environment.getExternalStorageDirectory().getPath() + "/checkupdatelib").show();

    }

    /**
     * 非强制更新
     */
    private static void UpdateDialogClick(Context context) {
        new UpdateDialog(context)
                .setAppSize(mCheckUpdateInfo.getNewAppSize())
                .setDownloadUrl(mCheckUpdateInfo.getNewAppUrl())
                .setTitle(mCheckUpdateInfo.getAppName())
                .setReleaseTime(mCheckUpdateInfo.getNewAppReleaseTime())
                .setVersionName(mCheckUpdateInfo.getNewAppVersionName())
                .setUpdateDesc(mCheckUpdateInfo.getNewAppUpdateDesc())
                .setFileName(App.getCtx().getString(R.string.app_name) + ".apk")
                .setFilePath(Environment.getExternalStorageDirectory().getPath() + "/checkupdatelib")
                //该方法需设为true,才会在通知栏显示下载进度,默认为false,即不显示
                //该方法只会控制下载进度的展示,当下载完成或下载失败时展示的通知不受该方法影响
                //即不管该方法是置为false还是true,当下载完成或下载失败时都会在通知栏展示一个通知
                .setShowProgress(true)
                .setIconResId(R.mipmap.ic_launcher)
                .setAppName(mCheckUpdateInfo.getAppName()).show();
    }


rsa和aes使用的方法如下:
#第一步
导入公钥pem，获取公钥后用来加密aes
#第二步
随机生成一个AES密钥，拿到Aes密钥后加密要传输的内容
#第三步
将被加密的内容，以及AES密钥和RSA公钥进行传输到服务器解密内容
#-----------------------------------------------下面是具体的代码实现-----------------------------------------------
//获取Rsa公钥，用来加密Aes的key，AES用来加密传输的内容

        RSAPublicKey publicKey = null;
        String encryptAESkey = null;
        String encryptRSAKey = null;
        try {
            publicKey = Tools.RSAUtils.loadPublicKey(App.getAppCtx().getAssets().open("rsa_public_key.pem"));
            //生成一个密钥随机的
            encryptAESkey = Tools.AESUtil.generateKeyString();
            encryptRSAKey = Tools.RSAUtils.encryptByPublicKey(encryptAESkey, publicKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //获取Gson的实例
        final GsonProvider gsonProvider = GsonProvider.getInstance();
        final Gson gson = gsonProvider.getGson();
        final String encryptInfo = gson.toJson(t);
        final String encryptContent = Tools.AESUtil.encrypt(encryptInfo, encryptAESkey);
        final UpEncryptInfo info = new UpEncryptInfo(encryptContent, encryptRSAKey,encryptAESkey);
        
         //目标项是否在最后一个可见项之后
            private boolean mShouldScroll;
            //记录目标项位置
            private int mToPosition;
        
            /**
             * 滑动到指定位置
             */
            private void smoothMoveToPosition(final int position) {
                // 第一个可见位置
                int firstItem = mRv.getChildLayoutPosition(mRv.getChildAt(0));
                // 最后一个可见位置
                int lastItem = mRv.getChildLayoutPosition(mRv.getChildAt(mRv.getChildCount() - 1));
                if (position < firstItem) {
                    // 第一种可能:跳转位置在第一个可见位置之前，使用smoothScrollToPosition
                    mRv.smoothScrollToPosition(position);
                } else if (position <= lastItem) {
                    // 第二种可能:跳转位置在第一个可见位置之后，最后一个可见项之前
                    int movePosition = position - firstItem;
                    if (movePosition >= 0 && movePosition < mRv.getChildCount()) {
                        int top = mRv.getChildAt(movePosition).getTop();
                        // smoothScrollToPosition 不会有效果，此时调用smoothScrollBy来滑动到指定位置
                        mRv.smoothScrollBy(0, top);
                    }
                } else {
                    // 第三种可能:跳转位置在最后可见项之后，则先调用smoothScrollToPosition将要跳转的位置滚动到可见位置
                    // 再通过onScrollStateChanged控制再次调用smoothMoveToPosition，执行上一个判断中的方法
                    mRv.smoothScrollToPosition(position);
                    mToPosition = position;
                    mShouldScroll = true;
                }
            }
