package band.kessokuteatime.lightemittingtriode.content;

import band.kessokuteatime.lightemittingtriode.LightEmittingTriode;
import band.kessokuteatime.lightemittingtriode.VoxelShaper;
import band.kessokuteatime.lightemittingtriode.content.block.decorational.FacingLampBlock;
import band.kessokuteatime.lightemittingtriode.content.block.decorational.LampBlock;
import band.kessokuteatime.lightemittingtriode.content.block.decorational.SlabFacingLampBlock;
import band.kessokuteatime.lightemittingtriode.content.block.functional.ButtonLampBlock;
import band.kessokuteatime.lightemittingtriode.content.block.functional.DetectorLampBlock;
import band.kessokuteatime.lightemittingtriode.content.block.functional.SwitchLampBlock;
import band.kessokuteatime.lightemittingtriode.content.item.ColoredBlockItem;
import band.kessokuteatime.lightemittingtriode.content.item.InventoryColoredBlockItem;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.data.server.recipe.*;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.Registries;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.Collectors;

public enum Variant {
    CLEAR("clear", size -> 15,
            size -> VoxelShapes.fullCube(),
            LampBlock::new,
            (block, dyeColor) -> new ColoredBlockItem(block, new Item.Settings(), dyeColor),

            wrapper -> ShapelessRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, wrapper.block())
                    .input(ModRegistries.Items.LET, 4)
                    .input(wrapper.dye())
                    .criterion(FabricRecipeProvider.hasItem(ModRegistries.Items.LET),
                            FabricRecipeProvider.conditionsFromItem(ModRegistries.Items.LET))
                    .criterion(FabricRecipeProvider.hasItem(wrapper.dye()),
                            FabricRecipeProvider.conditionsFromItem(wrapper.dye())),

            null
    ),
    SLAB("slab", size -> 13,
            size -> VoxelShaper.fromBottomCenter(16, 8),
            SlabFacingLampBlock::new,
            (block, dyeColor) -> new ColoredBlockItem(block, new Item.Settings(), dyeColor),

            wrapper -> ShapelessRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, wrapper.block())
                    .input(ModRegistries.Items.LET, 2)
                    .input(wrapper.dye())
                    .criterion(FabricRecipeProvider.hasItem(ModRegistries.Items.LET),
                            FabricRecipeProvider.conditionsFromItem(ModRegistries.Items.LET))
                    .criterion(FabricRecipeProvider.hasItem(wrapper.dye()),
                            FabricRecipeProvider.conditionsFromItem(wrapper.dye())),

            wrapper -> SmithingTransformRecipeJsonBuilder.create(
                            Ingredient.empty(),
                            Ingredient.ofItems(wrapper.blockItem()), Ingredient.ofItems(ModRegistries.Items.LET),
                            RecipeCategory.BUILDING_BLOCKS,
                            wrapper.blockItem(Variant.CLEAR.with(Size.NORMAL), wrapper.dyeColor())
                    )
                    .criterion(FabricRecipeProvider.hasItem(wrapper.blockItem()),
                            FabricRecipeProvider.conditionsFromItem(wrapper.blockItem()))
                    .criterion(FabricRecipeProvider.hasItem(ModRegistries.Items.LET),
                            FabricRecipeProvider.conditionsFromItem(ModRegistries.Items.LET))
    ),
    CEILING("ceiling", size -> 10,
            size -> VoxelShaper.fromBottomCenter(16, 1),
            FacingLampBlock::new,
            (block, dyeColor) -> new ColoredBlockItem(block, new Item.Settings(), dyeColor),

            wrapper -> ShapelessRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, wrapper.block())
                    .input(ModRegistries.Items.LET)
                    .input(wrapper.dye())
                    .criterion(FabricRecipeProvider.hasItem(ModRegistries.Items.LET),
                            FabricRecipeProvider.conditionsFromItem(ModRegistries.Items.LET))
                    .criterion(FabricRecipeProvider.hasItem(wrapper.dye()),
                            FabricRecipeProvider.conditionsFromItem(wrapper.dye())),

            wrapper -> SmithingTransformRecipeJsonBuilder.create(
                            Ingredient.empty(),
                            Ingredient.ofItems(wrapper.blockItem()), Ingredient.ofItems(ModRegistries.Items.LET),
                            RecipeCategory.BUILDING_BLOCKS,
                            wrapper.blockItem(Variant.SLAB.with(Size.NORMAL), wrapper.dyeColor())
                    )
                    .criterion(FabricRecipeProvider.hasItem(wrapper.blockItem()),
                            FabricRecipeProvider.conditionsFromItem(wrapper.blockItem()))
                    .criterion(FabricRecipeProvider.hasItem(ModRegistries.Items.LET),
                            FabricRecipeProvider.conditionsFromItem(ModRegistries.Items.LET))
    ),
    LANTERN("lantern", size -> 8 + size * 2,
            size -> VoxelShaper.fromBottomCenter(4 + 2 * size, 6 + size),
            FacingLampBlock::new,
            (block, dyeColor) -> new ColoredBlockItem(block, new Item.Settings(), dyeColor),

            wrapper -> ShapelessRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, wrapper.block())
                    .input(Items.IRON_NUGGET)
                    .input(ModRegistries.Items.LED, 1 + wrapper.basis().size().getSize())
                    .input(wrapper.dye())
                    .criterion(FabricRecipeProvider.hasItem(Items.IRON_NUGGET),
                            FabricRecipeProvider.conditionsFromItem(Items.IRON_NUGGET))
                    .criterion(FabricRecipeProvider.hasItem(ModRegistries.Items.LED),
                            FabricRecipeProvider.conditionsFromItem(ModRegistries.Items.LED))
                    .criterion(FabricRecipeProvider.hasItem(wrapper.dye()),
                            FabricRecipeProvider.conditionsFromItem(wrapper.dye())),

            wrapper -> wrapper.basis().size().getSize() >= 2 ? null : SmithingTransformRecipeJsonBuilder.create(
                            Ingredient.empty(),
                            Ingredient.ofItems(wrapper.blockItem()), Ingredient.ofItems(ModRegistries.Items.LED),
                            RecipeCategory.BUILDING_BLOCKS,
                            wrapper.blockItem(Variant.valueOf("LANTERN").with(wrapper.basis().size().larger()), wrapper.dyeColor())
                    )
                    .criterion(FabricRecipeProvider.hasItem(wrapper.blockItem()),
                            FabricRecipeProvider.conditionsFromItem(wrapper.blockItem()))
                    .criterion(FabricRecipeProvider.hasItem(ModRegistries.Items.LED),
                            FabricRecipeProvider.conditionsFromItem(ModRegistries.Items.LED))
    ),
    ALARM("alarm", size -> 5 + size * 3,
            size -> VoxelShaper.fromBottomCenter(10 + 2 * size, 1),
            FacingLampBlock::new,
            (block, dyeColor) -> new ColoredBlockItem(block, new Item.Settings(), dyeColor),

            wrapper -> ShapelessRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, wrapper.block())
                    .input(Items.GOLD_NUGGET)
                    .input(ModRegistries.Items.LED, 1 + wrapper.basis().size().getSize())
                    .input(wrapper.dye())
                    .criterion(FabricRecipeProvider.hasItem(Items.GOLD_NUGGET),
                            FabricRecipeProvider.conditionsFromItem(Items.GOLD_NUGGET))
                    .criterion(FabricRecipeProvider.hasItem(ModRegistries.Items.LED),
                            FabricRecipeProvider.conditionsFromItem(ModRegistries.Items.LED))
                    .criterion(FabricRecipeProvider.hasItem(wrapper.dye()),
                            FabricRecipeProvider.conditionsFromItem(wrapper.dye())),

            wrapper -> wrapper.basis().size().getSize() >= 2 ? null : SmithingTransformRecipeJsonBuilder.create(
                            Ingredient.empty(),
                            Ingredient.ofItems(wrapper.blockItem()), Ingredient.ofItems(ModRegistries.Items.LED),
                            RecipeCategory.BUILDING_BLOCKS,
                            wrapper.blockItem(Variant.valueOf("ALARM").with(wrapper.basis().size().larger()), wrapper.dyeColor())
                    )
                    .criterion(FabricRecipeProvider.hasItem(wrapper.blockItem()),
                            FabricRecipeProvider.conditionsFromItem(wrapper.blockItem()))
                    .criterion(FabricRecipeProvider.hasItem(ModRegistries.Items.LED),
                            FabricRecipeProvider.conditionsFromItem(ModRegistries.Items.LED))
    ),
    SWITCH("switch", size -> 1,
            size -> VoxelShaper.fromBottomCenter(8, 2),
            SwitchLampBlock::new,
            (block, dyeColor) -> new InventoryColoredBlockItem(block, new Item.Settings(), dyeColor),

            wrapper -> ShapelessRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, wrapper.block())
                    .input(Items.QUARTZ)
                    .input(ModRegistries.Items.TUBE)
                    .input(wrapper.dye())
                    .criterion(FabricRecipeProvider.hasItem(Items.QUARTZ),
                            FabricRecipeProvider.conditionsFromItem(Items.QUARTZ))
                    .criterion(FabricRecipeProvider.hasItem(ModRegistries.Items.TUBE),
                            FabricRecipeProvider.conditionsFromItem(ModRegistries.Items.TUBE))
                    .criterion(FabricRecipeProvider.hasItem(wrapper.dye()),
                            FabricRecipeProvider.conditionsFromItem(wrapper.dye())),

            null
    ),
    BUTTON("button", size -> 1,
            size -> Block.createCuboidShape(6, 0, 5, 10, 2, 11),
            wrapper -> new ButtonLampBlock(wrapper, 22),
            (block, dyeColor) -> new InventoryColoredBlockItem(block, new Item.Settings(), dyeColor),

            wrapper -> ShapelessRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, wrapper.block())
                    .input(ModRegistries.Items.TUBE)
                    .input(wrapper.dye())
                    .criterion(FabricRecipeProvider.hasItem(ModRegistries.Items.TUBE),
                            FabricRecipeProvider.conditionsFromItem(ModRegistries.Items.TUBE))
                    .criterion(FabricRecipeProvider.hasItem(wrapper.dye()),
                            FabricRecipeProvider.conditionsFromItem(wrapper.dye())),

            wrapper -> SmithingTransformRecipeJsonBuilder.create(
                            Ingredient.empty(),
                            Ingredient.ofItems(wrapper.blockItem()), Ingredient.ofItems(Items.QUARTZ),
                            RecipeCategory.BUILDING_BLOCKS,
                            wrapper.blockItem(Variant.SWITCH.with(Size.NORMAL), wrapper.dyeColor())
                    )
                    .criterion(FabricRecipeProvider.hasItem(wrapper.blockItem()),
                            FabricRecipeProvider.conditionsFromItem(wrapper.blockItem()))
                    .criterion(FabricRecipeProvider.hasItem(Items.QUARTZ),
                            FabricRecipeProvider.conditionsFromItem(Items.QUARTZ))
    ),
    DETECTOR("detector", size -> 1,
            size -> VoxelShaper.fromBottomCenter(16, 1),
            DetectorLampBlock::new,
            (block, dyeColor) -> new ColoredBlockItem(block, new Item.Settings(), dyeColor),

            wrapper -> ShapelessRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, wrapper.block())
                    .input(ModRegistries.Items.TUBE, 2)
                    .input(ModRegistries.Items.SHADE)
                    .input(Items.GOLD_NUGGET)
                    .input(wrapper.dye())
                    .criterion(FabricRecipeProvider.hasItem(ModRegistries.Items.TUBE),
                            FabricRecipeProvider.conditionsFromItem(ModRegistries.Items.TUBE))
                    .criterion(FabricRecipeProvider.hasItem(ModRegistries.Items.SHADE),
                            FabricRecipeProvider.conditionsFromItem(ModRegistries.Items.SHADE))
                    .criterion(FabricRecipeProvider.hasItem(Items.GOLD_NUGGET),
                            FabricRecipeProvider.conditionsFromItem(Items.GOLD_NUGGET))
                    .criterion(FabricRecipeProvider.hasItem(wrapper.dye()),
                            FabricRecipeProvider.conditionsFromItem(wrapper.dye())),

            null
    );

    public record Basis(Variant variant, Size size) {
        private String genericIdString(String... postfixes) {
            return variant().getId()
                    + size().getId().map(p -> "_" + p).orElse("")
                    + Arrays.stream(postfixes)
                    .filter(Objects::nonNull)
                    .filter(s -> !s.isBlank())
                    .map(s -> "_" + s)
                    .collect(Collectors.joining());
        }

        public Identifier genericId(String... postfixes) {
            return LightEmittingTriode.id("block", genericIdString(postfixes));
        }

        public Wrapper with(DyeColor dyeColor) {
            return new Wrapper(this, dyeColor);
        }
    }

    public record Wrapper(Basis basis, DyeColor dyeColor) {
        public ArrayList<Wrapper> wrappersOfOtherColors() {
            return Arrays.stream(DyeColor.values())
                    .filter(d -> d != dyeColor())
                    .map(d -> new Wrapper(basis(), d))
                    .collect(Collectors.toCollection(ArrayList::new));
        }

        public String idString(Basis basis, DyeColor dyeColor) {
            return basis.variant().getId()
                    + basis.size().getId().map(p -> "_" + p).orElse("")
                    + "_" + dyeColor.getName();
        }

        public String idString() {
            return idString(basis(), dyeColor());
        }

        public Identifier id() {
            return LightEmittingTriode.id(idString());
        }

        public Identifier categorizedId(String... categories) {
            ArrayList<String> paths = new ArrayList<>(List.of(categories));
            paths.add(idString());

            return LightEmittingTriode.id(paths.toArray(new String[]{}));
        }

        public int color() {
            return LightEmittingTriode.getColorFromDyeColor(dyeColor());
        }

        public int colorOverlay(boolean lit, int tintIndex) {
            return switch (tintIndex) {
                default -> color();
                // Outer
                case 0 -> LightEmittingTriode.mapColorRange(color(), lit ? 0xAF : 0, lit ? 0 : 0xAB);
                // Inner
                case 1 -> LightEmittingTriode.mapColorRange(color(), lit ? 0x7A : 0x10, lit ? 0x10 : 0x6D);
            };
        }

        public Vec3d generateSurfaceParticlePos(BlockState state, BlockView world, BlockPos pos, ShapeContext context, Random random, double factor) {
            return LightEmittingTriode.generateSurfaceParticlePos(pos, block().getOutlineShape(state, world, pos, context), random, factor);
        }

        public int luminance() {
            return basis().variant().getLuminance(basis().size().getSize());
        }

        public VoxelShape voxelShape() {
            return basis().variant().getVoxelShape(basis().size().getSize());
        }

        public Block createBlock() {
            return basis().variant().getBlock(this);
        }

        public BlockItem createBlockItem(Block block) {
            return basis().variant().getBlockItem(block, dyeColor());
        }

        public Block block(Basis basis, DyeColor dyeColor) {
            return Registries.BLOCK.get(LightEmittingTriode.id(idString(basis, dyeColor)));
        }

        public Block block() {
            return block(basis(), dyeColor());
        }

        public BlockItem blockItem(Basis basis, DyeColor dyeColor) {
            return (BlockItem) Registries.ITEM.get(LightEmittingTriode.id(idString(basis, dyeColor)));
        }

        public BlockItem blockItem() {
            return blockItem(basis(), dyeColor());
        }

        public Item dye() {
            return Registries.ITEM.get(new Identifier(dyeColor().getName() + "_dye"));
        }

        public Consumer<Consumer<RecipeJsonProvider>> useCraftingRecipeJsonBuilder() {
            return exporter -> Optional.ofNullable(basis().variant().craftingRecipeJsonBuilder(this))
                    .ifPresent(builder -> builder.offerTo(exporter, categorizedId("crafting")));
        }

        public Consumer<Consumer<RecipeJsonProvider>> useUpgradingRecipeJsonBuilder() {
            return exporter -> Optional.ofNullable(basis().variant().upgradingRecipeJsonBuilder(this))
                    .ifPresent(builder -> builder.offerTo(exporter, categorizedId("upgrading")));
        }

        public Consumer<Consumer<RecipeJsonProvider>> useRecoloringRecipeJsonBuilders() {
            return exporter -> Optional.ofNullable(basis().variant().recoloringRecipeJsonBuilder())
                    .ifPresent(builder ->
                            wrappersOfOtherColors().stream()
                                    .map(wrapper -> new AbstractMap.SimpleEntry<>(wrapper, builder.apply(Ingredient.ofItems(blockItem()), wrapper)))
                                    .forEach(entry -> entry.getValue().offerTo(
                                            exporter, 
                                            categorizedId("recoloring", "to_" + entry.getKey().dyeColor().getName())
                                    ))
            );
        }
    }

    public enum Size {
        NORMAL(null, 1),
        SMALL("small", 0),
        LARGE("large", 2);

        final @Nullable String id;
        final int size;

        Size(@Nullable String id, int size) {
            this.id = id;
            this.size = size;
        }

        public Optional<String> getId() {
            return id != null ? Optional.of(id) : Optional.empty();
        }

        public int getSize() {
            return size;
        }

        public Size get(int size) {
            return Arrays.stream(values()).filter(s -> s.getSize() == size).findFirst().orElse(NORMAL);
        }

        public Size larger() {
            return get(MathHelper.clamp(getSize() + 1, 0, 2));
        }

        public Size smaller() {
            return get(MathHelper.clamp(getSize() - 1, 0, 2));
        }
    }

    final String id;
    final IntFunction<Integer> luminance;
    final Function<Integer, VoxelShape> voxelShapeProvider;
    final Function<Wrapper, Block> blockProvider;
    final BiFunction<Block, DyeColor, BlockItem> blockItemProvider;
    @Nullable final Function<Wrapper, CraftingRecipeJsonBuilder> craftingRecipeJsonBuilder;
    @Nullable final Function<Wrapper, SmithingTransformRecipeJsonBuilder> upgradingRecipeJsonBuilder;
    @Nullable final BiFunction<Ingredient, Wrapper, SmithingTransformRecipeJsonBuilder> recoloringRecipeJsonBuilder = (ingredient, wrapper) ->
            SmithingTransformRecipeJsonBuilder.create(
                            Ingredient.empty(),
                            ingredient, Ingredient.ofItems(wrapper.dye()),
                            RecipeCategory.BUILDING_BLOCKS,
                            wrapper.blockItem(wrapper.basis(), wrapper.dyeColor())
                    )
                    .criterion(FabricRecipeProvider.hasItem(wrapper.blockItem()),
                            FabricRecipeProvider.conditionsFromItem(wrapper.blockItem()))
                    .criterion(FabricRecipeProvider.hasItem(wrapper.dye()),
                            FabricRecipeProvider.conditionsFromItem(wrapper.dye()));

    Variant(
            String id, IntFunction<Integer> luminance,
            Function<Integer, VoxelShape> voxelShapeProvider,
            Function<Wrapper, Block> blockProvider,
            BiFunction<Block, DyeColor, BlockItem> blockItemProvider,
            @Nullable Function<Wrapper, CraftingRecipeJsonBuilder> craftingRecipeJsonBuilder,
            @Nullable Function<Wrapper, SmithingTransformRecipeJsonBuilder> upgradingRecipeJsonBuilder
    ) {
        this.id = id;
        this.luminance = luminance;
        this.voxelShapeProvider = voxelShapeProvider;
        this.blockProvider = blockProvider;
        this.blockItemProvider = blockItemProvider;
        this.craftingRecipeJsonBuilder = craftingRecipeJsonBuilder;
        this.upgradingRecipeJsonBuilder = upgradingRecipeJsonBuilder;
    }

    public Basis with(Size size) {
        return new Basis(this, size);
    }

    public String getId() {
        return id;
    }

    public int getLuminance(int size) {
        return luminance.apply(size);
    }

    public VoxelShape getVoxelShape(int size) {
        return voxelShapeProvider.apply(size);
    }

    public Block getBlock(Wrapper wrapper) {
        return blockProvider.apply(wrapper);
    }

    public BlockItem getBlockItem(Block block, DyeColor dyeColor) {
        return blockItemProvider.apply(block, dyeColor);
    }

    public @Nullable CraftingRecipeJsonBuilder craftingRecipeJsonBuilder(Wrapper wrapper) {
        return craftingRecipeJsonBuilder == null ? null : craftingRecipeJsonBuilder.apply(wrapper);
    }

    public @Nullable SmithingTransformRecipeJsonBuilder upgradingRecipeJsonBuilder(Wrapper wrapper) {
        return upgradingRecipeJsonBuilder == null ? null : upgradingRecipeJsonBuilder.apply(wrapper);
    }

    public @Nullable BiFunction<Ingredient, Wrapper, SmithingTransformRecipeJsonBuilder> recoloringRecipeJsonBuilder() {
        return recoloringRecipeJsonBuilder;
    }
}
