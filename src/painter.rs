use egui::epaint::{Primitive, Vertex};
use egui::{ClippedPrimitive, TextureId};

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

#[derive(Debug)]
pub struct EguiPainter {
    pub(crate) before_handler: PainterBeforeHandler,
    pub(crate) mesh_handler: PainterMeshHandler,
    pub(crate) after_handler: PainterVoidHandler,
    pub(crate) pixels_per_point: f32,
}

impl EguiPainter {
    pub fn new(
        before_handler: PainterBeforeHandler,
        mesh_handler: PainterMeshHandler,
        after_handler: PainterVoidHandler,
    ) -> Self {
        Self {
            before_handler,
            mesh_handler,
            after_handler,
            pixels_per_point: 1.0,
        }
    }
    pub fn paint_primitive(&self, primitive: ClippedPrimitive) {
        if let Primitive::Mesh(mesh) = primitive.primitive {
            if (self.before_handler)() {
                for mesh in mesh.split_to_u16() {
                    let indices_len = mesh.indices.len();
                    let vertices_len = mesh.vertices.len();
                    let texture_managed = match mesh.texture_id {
                        TextureId::Managed(_) => true,
                        TextureId::User(_) => false,
                    };
                    let texture_id = match mesh.texture_id {
                        TextureId::Managed(id) => id,
                        TextureId::User(id) => id,
                    };
                    let rect = primitive.clip_rect;
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
