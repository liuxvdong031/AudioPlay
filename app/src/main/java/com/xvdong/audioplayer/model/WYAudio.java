package com.xvdong.audioplayer.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xvDong on 2023/9/13.
 */

public class WYAudio {

    private ResultBean result;
    private int code;

    public ResultBean getResult() {
        return result;
    }

    public void setResult(ResultBean result) {
        this.result = result;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public static class ResultBean {

        private int songCount;
        private List<SongsBean> songs;

        public int getSongCount() {
            return songCount;
        }

        public void setSongCount(int songCount) {
            this.songCount = songCount;
        }

        public List<SongsBean> getSongs() {
            if (songs == null) {
                return new ArrayList<>();
            }
            return songs;
        }

        public void setSongs(List<SongsBean> songs) {
            this.songs = songs;
        }

        public static class SongsBean {
            private String name;
            private int id;
            private AlbumBean album;
            private int mvid;
            private int rtype;
            private String rurl;
            private String mp3Url;
            private List<ArtistsBean> artists;

            public String getName() {
                return name == null ? "" : name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public AlbumBean getAlbum() {
                return album;
            }

            public void setAlbum(AlbumBean album) {
                this.album = album;
            }

            public int getMvid() {
                return mvid;
            }

            public void setMvid(int mvid) {
                this.mvid = mvid;
            }

            public int getRtype() {
                return rtype;
            }

            public void setRtype(int rtype) {
                this.rtype = rtype;
            }

            public String getRurl() {
                return rurl;
            }

            public void setRurl(String rurl) {
                this.rurl = rurl;
            }

            public String getMp3Url() {
                return mp3Url == null ? "" : mp3Url;
            }

            public void setMp3Url(String mp3Url) {
                this.mp3Url = mp3Url;
            }

            public List<ArtistsBean> getArtists() {
                if (artists == null) {
                    return new ArrayList<>();
                }
                return artists;
            }

            public void setArtists(List<ArtistsBean> artists) {
                this.artists = artists;
            }

            public static class AlbumBean implements Serializable {

                private String name;
                private int id;
                private String type;
                private int size;
                private long picId;
                private String blurPicUrl;
                private int companyId;
                private long pic;
                private String picUrl;

                public String getName() {
                    return name == null ? "" : name;
                }

                public void setName(String name) {
                    this.name = name;
                }

                public int getId() {
                    return id;
                }

                public void setId(int id) {
                    this.id = id;
                }

                public String getType() {
                    return type == null ? "" : type;
                }

                public void setType(String type) {
                    this.type = type;
                }

                public int getSize() {
                    return size;
                }

                public void setSize(int size) {
                    this.size = size;
                }

                public long getPicId() {
                    return picId;
                }

                public void setPicId(long picId) {
                    this.picId = picId;
                }

                public String getBlurPicUrl() {
                    return blurPicUrl == null ? "" : blurPicUrl;
                }

                public void setBlurPicUrl(String blurPicUrl) {
                    this.blurPicUrl = blurPicUrl;
                }

                public int getCompanyId() {
                    return companyId;
                }

                public void setCompanyId(int companyId) {
                    this.companyId = companyId;
                }

                public long getPic() {
                    return pic;
                }

                public void setPic(long pic) {
                    this.pic = pic;
                }

                public String getPicUrl() {
                    return picUrl == null ? "" : picUrl;
                }

                public void setPicUrl(String picUrl) {
                    this.picUrl = picUrl;
                }
            }

            public static class ArtistsBean implements Serializable {

                private String name;
                private int id;
                private int picId;
                private int img1v1Id;
                private String briefDesc;
                private String picUrl;
                private String img1v1Url;
                private int albumSize;
                private String trans;
                private int musicSize;

                public String getName() {
                    return name == null ? "" : name;
                }

                public void setName(String name) {
                    this.name = name;
                }

                public int getId() {
                    return id;
                }

                public void setId(int id) {
                    this.id = id;
                }

                public int getPicId() {
                    return picId;
                }

                public void setPicId(int picId) {
                    this.picId = picId;
                }

                public int getImg1v1Id() {
                    return img1v1Id;
                }

                public void setImg1v1Id(int img1v1Id) {
                    this.img1v1Id = img1v1Id;
                }

                public String getBriefDesc() {
                    return briefDesc == null ? "" : briefDesc;
                }

                public void setBriefDesc(String briefDesc) {
                    this.briefDesc = briefDesc;
                }

                public String getPicUrl() {
                    return picUrl == null ? "" : picUrl;
                }

                public void setPicUrl(String picUrl) {
                    this.picUrl = picUrl;
                }

                public String getImg1v1Url() {
                    return img1v1Url == null ? "" : img1v1Url;
                }

                public void setImg1v1Url(String img1v1Url) {
                    this.img1v1Url = img1v1Url;
                }

                public int getAlbumSize() {
                    return albumSize;
                }

                public void setAlbumSize(int albumSize) {
                    this.albumSize = albumSize;
                }

                public String getTrans() {
                    return trans == null ? "" : trans;
                }

                public void setTrans(String trans) {
                    this.trans = trans;
                }

                public int getMusicSize() {
                    return musicSize;
                }

                public void setMusicSize(int musicSize) {
                    this.musicSize = musicSize;
                }
            }
        }
    }
}
