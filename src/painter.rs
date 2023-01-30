use egui::epaint::{ImageDelta, Primitive, Vertex};
use egui::{ClippedPrimitive, ImageData, TextureFilter, TextureId, TextureOptions};

pub type PainterMeshHandler32 = extern "C" fn(
    min_x: f32,
    min_y: f32,
    max_x: f32,
    max_y: f32,
    indices: *const u32,
    indices_len: usize,
    vertices: *const Vertex,
    vertices_len: usize,
    texture_managed: bool,
    texture_id: u64,
) -> ();

pub type PainterMeshHandler = extern "C" fn(
    min_x: f32,
    min_y: f32,
    max_x: f32,
    max_y: f32,
    indices: *const u16,
    indices_len: usize,
    vertices: *const Vertex,
    vertices_len: usize,
    texture_managed: bool,
    texture_id: u64,
) -> ();

pub type PainterBeforeHandler = extern "C" fn() -> bool;
pub type PainterVoidHandler = extern "C" fn() -> ();
#[repr(C, align(1))]
pub struct EguiTextureId {
    pub typ: u8,
    pub value: u64,
}
impl From<&TextureId> for EguiTextureId {
    fn from(value: &TextureId) -> Self {
        match value {
            TextureId::Managed(value) => Self {
                typ: 0,
                value: value.clone(),
            },
            TextureId::User(value) => Self {
                typ: 1,
                value: value.clone(),
            },
        }
    }
}
pub const EGUI_TEXTURE_ID_MANAGED: u8 = 0;
pub const EGUI_TEXTURE_ID_USER: u8 = 1;
#[repr(C, align(1))]
pub struct EguiImageData {
    /// must be color image
    // pub typ: u8,
    pub size: [usize; 2],
    pub pixels: *const u8,
    pub len: usize,
}

impl From<&ImageData> for EguiImageData {
    fn from(value: &ImageData) -> Self {
        match value {
            ImageData::Color(image) => {
                let data: &[u8] = bytemuck::cast_slice(image.pixels.as_ref());
                Self {
                    size: image.size.clone(),
                    pixels: data.as_ptr(),
                    len: data.len(),
                }
            }
            ImageData::Font(image) => {
                let data: Vec<u8> = image
                    .srgba_pixels(None)
                    .flat_map(|a| a.to_array())
                    .collect();
                Self {
                    size: image.size.clone(),
                    pixels: data.as_ptr(),
                    len: data.len(),
                }
            }
        }
    }
}
pub const EGUI_IMAGE_DATA_COLOR: u8 = 0;
pub const EGUI_IMAGE_DATA_FONT: u8 = 1;

pub const EGUI_TEXTURE_FILTER_NEAREST: u8 = 0;
pub const EGUI_TEXTURE_FILTER_LINEAR: u8 = 1;
#[repr(C, align(1))]
pub struct EguiTextureOptions {
    pub magnification: u8,
    pub minification: u8,
}
impl From<&TextureOptions> for EguiTextureOptions {
    fn from(value: &TextureOptions) -> Self {
        Self {
            magnification: match value.magnification {
                TextureFilter::Nearest => 0,
                TextureFilter::Linear => 1,
            },
            minification: match value.minification {
                TextureFilter::Nearest => 0,
                TextureFilter::Linear => 1,
            },
        }
    }
}
#[repr(C, align(1))]
pub struct EguiImageDelta {
    pub image: EguiImageData,
    pub options: EguiTextureOptions,
    pub pos: [usize; 2],
    pub pos_valid: bool,
}
impl From<&ImageDelta> for EguiImageDelta {
    fn from(value: &ImageDelta) -> Self {
        Self {
            image: (&value.image).into(),
            options: (&value.options).into(),
            pos: match &value.pos {
                None => [0, 0],
                Some(pos) => pos.clone(),
            },
            pos_valid: false,
        }
    }
}
pub type SetTextureHandler = extern "C" fn(*const EguiTextureId, *const EguiImageDelta) -> ();
pub type FreeTextureHandler = extern "C" fn(*const EguiTextureId) -> ();

#[derive(Debug)]
pub struct EguiPainter {
    pub(crate) before_handler: PainterBeforeHandler,
    pub(crate) mesh_handler: PainterMeshHandler,
    pub(crate) after_handler: PainterVoidHandler,
    pub(crate) set_texture: SetTextureHandler,
    pub(crate) free_texture: FreeTextureHandler,
    pub(crate) pixels_per_point: f32,
}

impl EguiPainter {
    pub fn new(
        before_handler: PainterBeforeHandler,
        mesh_handler: PainterMeshHandler,
        after_handler: PainterVoidHandler,
        set_texture: SetTextureHandler,
        free_texture: FreeTextureHandler,
    ) -> Self {
        Self {
            before_handler,
            mesh_handler,
            after_handler,
            set_texture,
            free_texture,
            pixels_per_point: 1.0,
        }
    }
    pub fn paint_primitive(&self, primitive: ClippedPrimitive) {
        if let Primitive::Mesh(mesh) = primitive.primitive {
            if (self.before_handler)() {
                let rect = primitive.clip_rect;
                for mesh in mesh.split_to_u16() {
                    let indices_len = mesh.indices.len();
                    let vertices_len = mesh.vertices.len();
                    // info!("indices: {:?}", mesh.indices);
                    // info!("vertices: {:?}", mesh.vertices);
                    let mut points = Vec::with_capacity(mesh.vertices.len() * 2);
                    let mut uvs = Vec::with_capacity(mesh.vertices.len() * 2);
                    let mut colors = Vec::with_capacity(mesh.vertices.len());
                    for v in &mesh.vertices {
                        points.push(v.pos.x);
                        points.push(v.pos.y);
                        uvs.push(v.uv.x);
                        uvs.push(v.uv.y);
                        colors.push(u32::from_be_bytes(v.color.to_array()));
                    }
                    // info!("points: {:?}", points);
                    // info!("uvs: {:?}", uvs);
                    // info!("colors: {:?}", colors);

                    let texture_managed = match mesh.texture_id {
                        TextureId::Managed(_) => true,
                        TextureId::User(_) => false,
                    };
                    let texture_id = match mesh.texture_id {
                        TextureId::Managed(id) => id,
                        TextureId::User(id) => id,
                    };
                    (self.mesh_handler)(
                        rect.min.x,
                        rect.min.y,
                        rect.max.x,
                        rect.max.y,
                        mesh.indices.as_ptr(),
                        indices_len,
                        mesh.vertices.as_ptr(),
                        vertices_len,
                        texture_managed,
                        texture_id,
                    );
                }
                (self.after_handler)();
            }
        }
    }
}
